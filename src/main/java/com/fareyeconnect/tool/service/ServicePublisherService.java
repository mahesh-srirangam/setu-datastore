/*
 *
 *  * *
 *  *  * ****************************************************************************
 *  *  *
 *  *  * Copyright (c) 2023, FarEye and/or its affiliates. All rights
 *  *  * reserved.
 *  *  * ___________________________________________________________________________________
 *  *  *
 *  *  *
 *  *  * NOTICE: All information contained herein is, and remains the property of
 *  *  * FaEye and its suppliers,if any. The intellectual and technical concepts
 *  *  * contained herein are proprietary to FarEye. and its suppliers and
 *  *  * may be covered by us and Foreign Patents, patents in process, and are
 *  *  * protected by trade secret or copyright law. Dissemination of this information
 *  *  * or reproduction of this material is strictly forbidden unless prior written
 *  *  * permission is obtained from FarEye
 *  *
 *
 */

package com.fareyeconnect.tool.service;

import com.fareyeconnect.config.security.GatewayUser;
import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.exception.AppException;
import com.fareyeconnect.tool.dto.ServiceKey;
import com.fareyeconnect.tool.model.Connector;
import com.fareyeconnect.tool.model.Service;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.logging.Log;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.pubsub.ReactivePubSubCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * @author Hemanth Reddy
 * @since 16/01/23
 */
@Startup
public class ServicePublisherService implements Consumer<Service> {

    @Inject
    ServiceService serviceService;

    @Inject
    ConnectorService connectorService;

    @Inject
    @CacheName("service-cache")
    Cache cache;

    private final ReactiveValueCommands<ServiceKey, Service> serviceCommands;

    private final ReactivePubSubCommands<Service> pubSubCommands;

    private final ReactiveKeyCommands<ServiceKey> keyCommands;

    public ServicePublisherService(ReactiveRedisDataSource reactiveRedisDataSource) {
        keyCommands = reactiveRedisDataSource.key(ServiceKey.class);
        serviceCommands = reactiveRedisDataSource.value(ServiceKey.class, Service.class);
        pubSubCommands = reactiveRedisDataSource.pubsub(Service.class);
        pubSubCommands.subscribe("service", this).subscribe().with(consumer -> Log.info("Successfully subscribed "));
    }

    @ReactiveTransactional
    void onStart(@Observes StartupEvent event) {
        Log.info("Publishing services start");
        serviceService.findByActive(AppConstant.Status.LIVE.toString()).subscribe().with(serviceList ->
                serviceList.forEach(service -> cacheServicesOnStartup(service).subscribe()
                        .with(done -> Log.info("Number of services cached " + serviceList.size()))));
    }

    @Override
    public void accept(Service service) {
        Log.info("Received message " + service);
        ServiceKey serviceKey = buildServiceKey(service, service.getCreatedByOrg());
        cache.as(CaffeineCache.class).put(serviceKey, CompletableFuture.completedFuture(service));
    }

    public Uni<Void> cacheServicesOnStartup(Service service) {
        Log.infof("Caching service " + service);
        ServiceKey serviceKey = buildServiceKey(service, service.getCreatedByOrg());
        Uni<String> uniLocalCache = Uni.createFrom().item(publishLocalCache(serviceKey, service));
        Uni<Void> uniRedisCache = serviceCommands.set(serviceKey, service);
        Uni.combine().all().unis(uniLocalCache, uniRedisCache).asTuple().subscribe().with(tuple -> {
            Log.info("Local cache publish response " + tuple.getItem1());
            Log.info("Publish Response " + tuple.getItem2());
        }, error -> Log.error("Error while caching service " + error.getMessage()));
        return Uni.createFrom().voidItem();
    }

    private Uni<Void> publishServiceOnEvent(Service service) {
        ServiceKey serviceKey = buildServiceKey(service, GatewayUser.getUser().getOrganizationId());
        Uni<Void> uniRedisCache = serviceCommands.set(serviceKey, service);
        Uni<Void> publishServiceToRedis = pubSubCommands.publish("service", service);
        return Uni.combine().all().unis(uniRedisCache, publishServiceToRedis).discardItems();
    }

    private Uni<Void> invalidateDeployedVersion(Service service) {
        ServiceKey serviceKey = buildServiceKey(service, GatewayUser.getUser().getOrganizationId());
        Uni<Void> delLocalCache = cache.as(CaffeineCache.class).invalidate(serviceKey);
        Uni<Integer> delRedisCache = keyCommands.del(serviceKey);
        return Uni.combine().all().unis(delRedisCache, delLocalCache).discardItems();
    }

    public String publishLocalCache(ServiceKey serviceKey, Service service) {
        cache.as(CaffeineCache.class).put(serviceKey, CompletableFuture.completedFuture(service));
        return "Service Published " + service.getCode();
    }


    @ReactiveTransactional
    public Uni<Object> publish(Service service) {
        Uni<Service> publishedService = serviceService.findLiveService(service.getConnector(), service.getCode(), AppConstant.Status.LIVE.toString());
        return publishedService.onItem().transformToUni(item -> {
            if (item == null) {
                Log.info("No version for this service code is in live state " + service.getCode());
                return serviceService.update(service).onItem().transformToUni(this::publishServiceOnEvent);
            } else {
                Log.info("Published version available " + item.getCode() + " Version:: " + item.getVersion());
                item.setStatus(AppConstant.Status.STABLE.toString());
                return serviceService.update(item).onItem().transformToUni(updatedItem ->
                        serviceService.update(service).onItem().transformToUni(
                                liveVersion -> invalidateDeployedVersion(service).
                                        onItem().transformToUni(delCacheResult -> {
                                            Log.info("Publishing cache on publish");
                                            publishServiceOnEvent(service).subscribe().with(cache -> Log.info("Services cached"));
                                            return Uni.createFrom().voidItem();
                                        })
                        ));
            }
        });
    }

    public Uni<Service> fetchLiveVersion(String connectorCode, int connectorVersion, String serviceCode) throws ExecutionException, InterruptedException {
        Log.info(connectorCode + " " + serviceCode);
        String orgId = GatewayUser.getUser().getOrganizationId();
        ServiceKey serviceKey = buildServiceKey(connectorCode, connectorVersion, serviceCode, orgId);
        CompletableFuture<Service> liveService = cache.as(CaffeineCache.class).getIfPresent(serviceKey);
        if (liveService == null) {
            Log.info("Local cache is null " + liveService);
            Uni<Service> serviceUni = serviceCommands.get(serviceKey);
            return serviceUni.onItem().transformToUni(item -> {
                if (item == null) {
                    return connectorService.findByCodeAndVersion(connectorCode, connectorVersion).onItem()
                            .transformToUni(connector -> {
                                if (connector == null) throw new AppException("No connector found");
                                else {
                                    return serviceService.findLiveService((Connector) connector, serviceCode, AppConstant.Status.LIVE.toString())
                                            .onItem().transformToUni(serviceFromDb -> {
                                                if (serviceFromDb == null)
                                                    throw new AppException("No live service found");
                                                else return Uni.createFrom().item(serviceFromDb);
                                            });

                                }
                            });
                } else {
                    Log.info("Fetching service from redis cache ");
                    return Uni.createFrom().item(item);
                }
            });
        } else return Uni.createFrom().item(liveService.get());
    }

//    public CompletableFuture<Service> get(Service service) throws ExecutionException, InterruptedException {
//        ServiceKey serviceKey = new ServiceKey(service.getConnector().getCode(), service.getCode(), null);
//        return cache.as(CaffeineCache.class).getIfPresent(serviceKey);
//    }

    private ServiceKey buildServiceKey(Service service, String organizationId) {
        return ServiceKey.builder()
                .service(service.getCode())
                .connector(service.getConnector().getCode())
                .connectorVersion(service.getConnector().getVersion())
                .organizationId(organizationId)
                .build();
    }

    private ServiceKey buildServiceKey(String connectorCode, int connectorVersion, String serviceCode, String organizationId) {
        return ServiceKey.builder()
                .connector(connectorCode)
                .connectorVersion(connectorVersion)
                .service(serviceCode)
                .organizationId(organizationId)
                .build();
    }
}
