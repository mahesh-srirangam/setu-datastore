package com.fareyeconnect.controller;

import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


/**
 * @author srirangam
 * @since 30/12/22 9:50 am
 */

@ApplicationScoped
public class RabbitMQConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQConfiguration.class);

    @Inject
    @ConfigProperty(name = "kafka.bootstrap.server")
    String bootstrapServer;

    @Inject
    Vertx vertx;


    @Inject
    @ConfigProperty(name = "rabbitmq.queue")
    String topic;


    private RabbitMQClient consumer;

    void init() {
        LOG.info("Rabbit MQ Launched in the kart");
        consumer = RabbitMQClient.create(vertx, consumerConfig());
    }

    public RabbitMQClient getConsumer() {
        return consumer;
    }

    public String getQueue() {
        return topic;
    }


    private RabbitMQOptions consumerConfig() {
        RabbitMQOptions config = new RabbitMQOptions();
        config.setUser("user1");
        config.setPassword("password1");
        config.setHost("localhost");
        config.setPort(5672);
        config.setVirtualHost("vhost1");
        config.setConnectionTimeout(6000); // in milliseconds
        config.setRequestedHeartbeat(60); // in seconds
        config.setHandshakeTimeout(6000); // in milliseconds
        config.setRequestedChannelMax(5);
        config.setNetworkRecoveryInterval(500); // in milliseconds
        config.setAutomaticRecoveryEnabled(true);
        return config;
    }

    @PreDestroy
    void close() {
        if(consumer!=null){
            consumer.stop();
        }
    }
}
