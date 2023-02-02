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
import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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

    @Inject
    @ConfigProperty(name = "rabbitmq.queue")
    String queue;

    private RabbitMQClient consumer;

    @Inject
    RabbitQueueConsumer rabbitQueueConsumer;

    /**
     * Initialise the RabbitMQ Queue with configuration
     */
    @Override
    public void init() {
        Log.info("Rabbit MQ getting initialised");
        consumer = RabbitMQClient.create(vertx, consumerConfig());
        rabbitQueueConsumer.init();
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
    private RabbitMQOptions consumerConfig() {
        RabbitMQOptions config = new RabbitMQOptions();
        config.setUser("guest");
        config.setPort(5672);
        config.setPassword("guest");
        config.setHost("localhost");
        config.setVirtualHost("/");
        config.setConnectionTimeout(6000); // in milliseconds
        config.setRequestedHeartbeat(60); // in seconds
        config.setHandshakeTimeout(6000); // in milliseconds
        config.setRequestedChannelMax(5);
        config.setNetworkRecoveryInterval(500); // in milliseconds
        config.setAutomaticRecoveryEnabled(true);
        return config;
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
