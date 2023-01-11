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

import com.fareyeconnect.tool.dto.ServiceKey;
import com.fareyeconnect.tool.model.Service;
import io.quarkus.arc.Priority;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.logging.Log;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.pubsub.ReactivePubSubCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@ApplicationScoped
@Startup
public class ServicePublisherService implements Consumer<Service> {

    @Inject
    ServiceService serviceService;

    @Inject
    @CacheName("service-cache")
    Cache cache;

    private final ReactiveValueCommands<ServiceKey, Service> serviceCommands;

    private final ReactivePubSubCommands<Service> pubSubCommands;

    public ServicePublisherService(ReactiveRedisDataSource reactiveRedisDataSource) {
        serviceCommands = reactiveRedisDataSource.value(ServiceKey.class, Service.class);
        pubSubCommands = reactiveRedisDataSource.pubsub(Service.class);
        pubSubCommands.subscribe("service", this).subscribe().with(consumer -> Log.info("Successfully subscribed "));
    }

    void onStart(@Observes StartupEvent event)  {
        Log.infof("Fetching service start");
        //List<Service> serviceList = serviceService.findByActive("live").subscribeAsCompletionStage().get();
        serviceService.findByActive("live").subscribe().with(serviceList1 -> {
            Log.info("Service List size " + serviceList1.size());
            serviceList1.forEach(service -> cacheService(service).subscribe().with(done -> Log.info("Cached")));
        });

//        serviceService.findByActive("live").onItem().invoke(serviceList -> {
//                    Log.info("Size " + serviceList.size());
//                    serviceList.forEach(service -> cacheService(service).subscribe().with(done -> Log.info("Cached...")));
//                }
////                serviceList -> {
////                    serviceList.forEach(service -> cacheService(service).subscribe().with(
////                            message ->
////                            {
////                                Log.info("Service published " + service);
////                            },
////                            failure -> Log.error("Failed to publish service " + service))
////                    );
////                },
////                failure -> Log.error("Failed to fetch service " + failure)
//
//        ).subscribe().with(test -> Log.info("Tets"));
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
        Uni<String> uniCache = Uni.createFrom().item(publishLocalCache(service));
        uniCache.subscribe().with(done -> Log.info("Published"));
        ServiceKey serviceKey = new ServiceKey(service.getConnector().getCode(), service.getCode(), service.getCreatedByOrg());
        return serviceCommands.set(serviceKey, service);

    }

    public String publishLocalCache(Service service) {
        ServiceKey serviceKey = new ServiceKey(service.getConnector().getCode(), service.getCode(), service.getCreatedByOrg());
        cache.as(CaffeineCache.class).put(serviceKey, CompletableFuture.completedFuture(service));
        return "Service Published " + service.getCode();
    }

    public CompletableFuture<Object> getAllCacheKeys() {
        ServiceKey serviceKey = new ServiceKey("2go", "tracking-pull", "1");
        return cache.as(CaffeineCache.class).getIfPresent(serviceKey);
    }
}
