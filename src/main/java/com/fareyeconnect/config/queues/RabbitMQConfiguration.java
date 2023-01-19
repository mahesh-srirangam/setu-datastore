package com.fareyeconnect.config.queues;

import com.fareyeconnect.service.MessagingQueue;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
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
public class RabbitMQConfiguration implements MessagingQueue {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQConfiguration.class);

    @Inject
    Vertx vertx;

    @Inject
    @ConfigProperty(name = "rabbitmq.queue")
    String queue;

    private RabbitMQClient consumer;

    @Inject
    RabbitQueueConsumer rabbitMQConverter;

    /**
     * Initialise the RabbitMQ Queue with configuration
     */
    @Override
    public void init() {
        LOG.info("Rabbit MQ getting initialised");
        consumer = RabbitMQClient.create(vertx, consumerConfig());
        rabbitMQConverter.init();
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
                        LOG.info("Message published successfully!");
                    } else {
                        LOG.info("Failed to publish message: {}" , res.cause().getMessage());
                    }
                });
            } else {
                LOG.info("Failed to connect to RabbitMQ: {}", ar.cause().getMessage());
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
