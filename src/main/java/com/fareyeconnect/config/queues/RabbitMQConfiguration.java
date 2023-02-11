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

import com.fareyeconnect.service.MessagingQueue;
import com.fareyeconnect.tool.dto.Config;
import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


/**
 * @author srirangam
 * @since 30/12/22 9:50 am
 */

@ApplicationScoped
public class RabbitMQConfiguration implements MessagingQueue {

    @Inject
    Vertx vertx;

    private RabbitMQClient consumer;

    private String queue;

    @Inject
    RabbitQueueConsumer rabbitQueueConsumer;

    /**
     * Initialise the RabbitMQ Queue with configuration
     */
    @Override
    public void init(Config config) {
        Log.info("Rabbit MQ getting initialised");
        queue = config.getQueueName();
        consumer = RabbitMQClient.create(vertx, consumerConfig(config));
        rabbitQueueConsumer.init(consumer, queue);
    }

    /**
     * Return RabbitMQ Consumer
     * @return
     */
    public RabbitMQClient getConsumer() {
        return consumer;
    }


    /**
     * Returns the queue name
     * @return
     */
    public String getQueue() {
        return queue;
    }


    /**
     * Set the consumer configurations of the RabbitMQ
     * @return
     */
    private RabbitMQOptions consumerConfig(Config config) {
        RabbitMQOptions queueConfig = new RabbitMQOptions();
        queueConfig.setUser(config.getUser());
        queueConfig.setPort(config.getPort());
        queueConfig.setPassword(config.getPassword());
        queueConfig.setHost(config.getHost());
        queueConfig.setVirtualHost(config.getVirtualHost());
        queueConfig.setConnectionTimeout(6000); // in milliseconds
        queueConfig.setRequestedHeartbeat(60); // in seconds
        queueConfig.setHandshakeTimeout(6000); // in milliseconds
        queueConfig.setRequestedChannelMax(5);
        queueConfig.setNetworkRecoveryInterval(500); // in milliseconds
        queueConfig.setAutomaticRecoveryEnabled(true);
        return queueConfig;
    }

    /**
     * Producer configuration
     * Publish message
     */
    private void producerConfig() {
        RabbitMQOptions config =  new RabbitMQOptions()
                .setHost("localhost")
                .setPort(5672)
                .setUser("guest")
                .setPassword("guest");
        RabbitMQClient client = RabbitMQClient.create(vertx, config);
        client.start(ar -> {
            if (ar.succeeded()) {
                String message = "Hello, RabbitMQ!";
                String exchange = "TestExchange";
                String routingKey = "hello";

                client.basicPublish(exchange,routingKey, Buffer.buffer(message.getBytes()), res -> {
                    if (res.succeeded()) {
                        Log.info("Message published successfully!");
                    } else {
                        Log.info("Failed to publish message: {}" + res.cause().getMessage());
                    }
                });
            } else {
                Log.info("Failed to connect to RabbitMQ: {}"+ ar.cause().getMessage());
            }
        });
    }


    @PreDestroy
    void close() {
        if (consumer != null) {
            consumer.stop();
        }
    }
}
