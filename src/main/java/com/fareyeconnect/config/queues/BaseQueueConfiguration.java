/*
 * *
 *  * ****************************************************************************
 *  *
 *  * Copyright (c) 2023, FarEye and/or its affiliates. All rights
 *  * reserved.
 *  * ___________________________________________________________________________________
 *  *
 *  *
 *  * NOTICE: All information contained herein is, and remains the property of
 *  * FaEye and its suppliers,if any. The intellectual and technical concepts
 *  * contained herein are proprietary to FarEye. and its suppliers and
 *  * may be covered by us and Foreign Patents, patents in process, and are
 *  * protected by trade secret or copyright law. Dissemination of this information
 *  * or reproduction of this material is strictly forbidden unless prior written
 *  * permission is obtained from FarEye
 *
 */

package com.fareyeconnect.config.queues;


import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.service.MessagingQueueFactory;
import com.fareyeconnect.tool.dto.Config;
import com.fareyeconnect.tool.dto.ServiceKey;
import com.fareyeconnect.tool.service.ServicePublisherService;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * @author srirangam
 * @since 26/12/22 4:10 pm
 */
@ApplicationScoped
public class BaseQueueConfiguration {

    @Inject
    MessagingQueueFactory messagingQueueFactory;

    @Inject
    @CacheName("service-cache")
    Cache cache;

    @Inject
    ServicePublisherService servicePublisherService;

    void onStartUp(@Observes StartupEvent start){
        Log.info("onStart event");
        messagingQueueInit();
    }

    /**
     * Initialise the type of messaging queue at runtime
     * RabbitMQ, Kafka
     * can implement multiple queues in an instance
     */
    @PostConstruct
    public void messagingQueueInit(){
        Log.info("Messaging queue intialisation");
        servicePublisherService.cacheOnStart().subscribe().with(ele-> fetchFromCache(), failure-> Log.error("Failure occured in message queue intialisation:"+failure.getMessage()));
    }

    /**
     * Fetches from locally cached services
     * @return
     */
    private Uni<Void> fetchFromCache(){
        cache.as(CaffeineCache.class).keySet().parallelStream().forEach(serviceKey -> {
            ServiceKey service = (ServiceKey) serviceKey;
            try {
                servicePublisherService.fetchLiveVersion(service.getConnector(), service.getConnectorVersion(), service.getService()).subscribe().with(ele-> {
                    try {
                        initialiseMQ(ele.getConfig());
                    } catch (Exception e) {
                        Log.error("Error occured while initialising the queue:{}",e);
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.error("Exception occured while pulling the live version of the service:" + e.getMessage());
            }
        });
        return  Uni.createFrom().voidItem();
    }


    /**
     * Initialise the corresponding messaging queue from the config provided
     * @param config
     */
    private void initialiseMQ(Config config){
        String queueName = config.getQueueType();
        if(!StringUtils.isEmpty(queueName))
            Arrays.stream(queueName.split(","))
                .filter(e->!StringUtils.isEmpty(e))
                .forEach(msgQueue->messagingQueueFactory.getMessagingQueue(AppConstant.MessageQueue.valueOf(msgQueue.toUpperCase())).init(config));
    }

}
