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
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.ConfigValue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @author srirangam
 * @since 26/12/22 4:10 pm
 */
@ApplicationScoped
public class BaseQueueConfiguration {

    @Inject
    MessagingQueueFactory messagingQueueFactory;
    void onStartUp(@Observes StartupEvent start){
        Log.info("onStart event");
    }

    /**
     * Initialise the type of messaging queue at runtime
     * RabbitMQ, Kafka
     */
    @PostConstruct
    public void messagingQueueInit(){
        Log.info("Messaging queue intialisation");
        ConfigValue s = ConfigProvider.getConfig().getConfigValue("implement.queue");
        messagingQueueFactory.getMessagingQueue(AppConstant.MessageQueue.valueOf(s.getValue().toUpperCase())).init();
    }

}
