package com.fareyeconnect.controller;

import io.vertx.core.AsyncResult;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author srirangam
 * @since 30/12/22 10:11 am
 */

@ApplicationScoped
public class RabbitQueueConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitQueueConsumer.class);

    @Inject
    RabbitMQConfiguration rabbitMQConfiguration;

    @Inject
    @Channel("price-stream")
    Emitter<Double> priceEmitter;

    private static final double CONVERSION_RATE = .88;

    /**
     *  Consuming the messages from the rabbitmq queue
     */
    void init() {
        LOG.info("Initializing RabbitMQ Consumer");
        RabbitMQClient rabbitMQClient= rabbitMQConfiguration.getConsumer();
        QueueOptions options = new QueueOptions()
                .setMaxInternalQueueSize(1000)
                .setKeepMostRecent(true);

        rabbitMQClient.start(ar -> {
            if (ar.succeeded()) {
                rabbitMQClient.basicConsumer(rabbitMQConfiguration.getQueue(), options, rabbitMQConsumerAsyncResult -> {
                    if (rabbitMQConsumerAsyncResult.succeeded()) {
                        LOG.info("RabbitMQ consumer created !");
                            consumeMessage(rabbitMQConsumerAsyncResult);
                    } else {
                        rabbitMQConsumerAsyncResult.cause().printStackTrace();
                    }
                });
            } else {
                LOG.info("Failed to connect to RabbitMQ: {}" , ar.cause().getMessage());
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
            LOG.info("Got message: {}" ,message.body());
            double outputPrice = Double.parseDouble(message.body().toString()) * CONVERSION_RATE;
            LOG.info("Writing price {} to price-stream", outputPrice);
            priceEmitter.send(outputPrice);
        });
    }

}
