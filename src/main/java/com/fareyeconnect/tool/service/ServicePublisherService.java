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
import com.fareyeconnect.tool.dto.ServiceKey;
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
        serviceService.findByActive("live").subscribe().with(serviceList ->
                serviceList.forEach(service -> cacheService(service).subscribe()
                        .with(done -> Log.info("Number of services cached " + serviceList.size()))));
    }

    public void publishService(Service service) {
        Uni<Void> cacheResponse = cacheService(service);
        Uni<Void> publishResponse = pubSubCommands.publish("service", service);
        Uni.combine().all().unis(cacheResponse, publishResponse).asTuple().subscribe().with(tuple -> {
            Log.info("Cache Response " + tuple.getItem1());
            Log.info("Publish Response " + tuple.getItem2());
        }, error -> Log.error("Error while caching service " + error.getMessage()));
    }

    @Override
    public void accept(Service service) {
        Log.info("Received message " + service);
        ServiceKey serviceKey = new ServiceKey(service.getConnector().getCode(), service.getCode(), service.getCreatedByOrg());
        cache.as(CaffeineCache.class).put(serviceKey, CompletableFuture.completedFuture(service));
    }

    public Uni<Void> cacheService(Service service) {
        Log.infof("Caching service " + service);
        ServiceKey serviceKey = new ServiceKey(service.getConnector().getCode(), service.getCode(), service.getCreatedByOrg());
        Uni<String> uniLocalCache = Uni.createFrom().item(publishLocalCache(serviceKey, service));
        Uni<Void> uniRedisCache = serviceCommands.set(serviceKey, service);
        Uni.combine().all().unis(uniLocalCache, uniRedisCache).asTuple().subscribe().with(tuple -> {
            Log.info("Local cache publish response " + tuple.getItem1());
            Log.info("Publish Response " + tuple.getItem2());
        }, error -> Log.error("Error while caching service " + error.getMessage()));
        return Uni.createFrom().voidItem();
    }

    private Uni<Service> invalidateDeployedVersion(Service service) {
        ServiceKey serviceKey = new ServiceKey(service.getConnector().getCode(), service.getCode(), GatewayUser.getUser().getOrganizationId());
        cache.as(CaffeineCache.class).invalidate(serviceKey);
        keyCommands.del(serviceKey);
        return Uni.createFrom().item(service);
    }

    public String publishLocalCache(ServiceKey serviceKey, Service service) {
        cache.as(CaffeineCache.class).put(serviceKey, CompletableFuture.completedFuture(service));
        return "Service Published " + service.getCode();
    }

    private CompletableFuture<Object> getAllCacheKeys() {
        ServiceKey serviceKey = new ServiceKey("2go", "tracking-pull", "1");
        return cache.as(CaffeineCache.class).getIfPresent(serviceKey);
    }


    @ReactiveTransactional
    public Uni<Object> publish(Service service) {
        Uni<Service> publishedService = serviceService.findLiveService(service.getConnector(), service.getCode(), AppConstant.Status.LIVE.toString());
        return publishedService.onItem().transformToUni(item -> {
            if (item == null) {
                Log.info("No version for this service code is in live state " + service.getCode());
                return serviceService.update(service).onItem().transformToUni(this::cacheService);
            } else {
                Log.info("Published version available " + item.getCode() + " Version:: " + item.getVersion());
                item.setStatus(AppConstant.Status.STABLE.toString());
                return serviceService.update(item).onItem().transformToUni(updatedItem ->
                        serviceService.update(service).onItem().transformToUni(
                                liveVersion -> invalidateDeployedVersion(service).
                                        onItem().transformToUni(delCacheResult -> cacheService(service))
                        ));
            }
        });
    }

    public CompletableFuture<Service> get(Service service) {
        ServiceKey serviceKey = new ServiceKey(service.getConnector().getCode(), service.getCode(), null);
        return cache.as(CaffeineCache.class).getIfPresent(serviceKey);
    }
}
