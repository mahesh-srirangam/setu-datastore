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

import io.quarkus.logging.Log;
import io.vertx.core.AsyncResult;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author srirangam
 * @since 30/12/22 10:11 am
 */

@ApplicationScoped
public class RabbitQueueConsumer {


    /**
     *  Consuming the messages from the rabbitmq queue
     */
    void init(RabbitMQClient consumer, String queue) {
        Log.info("Initializing RabbitMQ Consumer");
        QueueOptions options = new QueueOptions()
                .setMaxInternalQueueSize(1000)
                .setKeepMostRecent(true);

        consumer.start(ar -> {
            if (ar.succeeded()) {
                consumer.basicConsumer(queue, options, rabbitMQConsumerAsyncResult -> {
                    if (rabbitMQConsumerAsyncResult.succeeded()) {
                        Log.info("RabbitMQ consumer created !");
                            consumeMessage(rabbitMQConsumerAsyncResult);
                    } else {
                        rabbitMQConsumerAsyncResult.cause().printStackTrace();
                    }
                });
            } else {
                Log.info("Failed to connect to RabbitMQ: {}" + ar.cause().getMessage());
            }
        });
    }


    /**
     * Consuming the message received from rabbitMQ queue
     * @param rabbitMQConsumerAsyncResult
     */
    private void consumeMessage(AsyncResult<RabbitMQConsumer> rabbitMQConsumerAsyncResult){
        rabbitMQConsumerAsyncResult.result().handler(message -> {
            Log.info("Got message: {}" +message.body());
            //invoke setu service
        });
    }

}
