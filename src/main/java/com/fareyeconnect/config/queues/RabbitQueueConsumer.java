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
import javax.inject.Inject;

/**
 *
 * @author srirangam
 * @since 30/12/22 10:11 am
 */

@ApplicationScoped
public class RabbitQueueConsumer {
    @Inject
    RabbitMQConfiguration rabbitMQConfiguration;

    private static final double CONVERSION_RATE = .88;

    /**
     *  Consuming the messages from the rabbitmq queue
     */
    void init() {
        Log.info("Initializing RabbitMQ Consumer");
        RabbitMQClient rabbitMQClient= rabbitMQConfiguration.getConsumer();
        QueueOptions options = new QueueOptions()
                .setMaxInternalQueueSize(1000)
                .setKeepMostRecent(true);

        rabbitMQClient.start(ar -> {
            if (ar.succeeded()) {
                rabbitMQClient.basicConsumer(rabbitMQConfiguration.getQueue(), options, rabbitMQConsumerAsyncResult -> {
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
        RabbitMQConsumer mqConsumer = rabbitMQConsumerAsyncResult.result();
        mqConsumer.handler(message -> {
            Log.info("Got message: {}" +message.body());
            double outputPrice = Double.parseDouble(message.body().toString()) * CONVERSION_RATE;
            Log.info("Writing price {} to price-stream"+ outputPrice);
        });
    }

}
