package com.fareyeconnect.controller;

import io.vertx.core.AsyncResult;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author srirangam
 * @since 30/12/22 10:11 am
 */

@ApplicationScoped
public class RabbitMQConverter {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQConverter.class);

    @Inject
    RabbitMQConfiguration rabbitMQConfiguration;

    @Inject
    @Channel("price-stream")
    Emitter<Double> priceEmitter;

    private static final double CONVERSION_RATE = .88;

    private boolean started = false;

    private RabbitMQClient priceConsumer;

    @PostConstruct
    void init() {
        LOG.info("Initializing PriceConverter");
        priceConsumer = rabbitMQConfiguration.getConsumer();
        QueueOptions options = new QueueOptions()
                .setMaxInternalQueueSize(1000)
                .setKeepMostRecent(true);

        priceConsumer.basicConsumer(rabbitMQConfiguration.getQueue(), options, rabbitMQConsumerAsyncResult -> {
            if (rabbitMQConsumerAsyncResult.succeeded()) {
                LOG.info("RabbitMQ consumer created !");
                consumeMessage(rabbitMQConsumerAsyncResult);
            } else {
                rabbitMQConsumerAsyncResult.cause().printStackTrace();
            }
        });

    }


    private void consumeMessage(AsyncResult<RabbitMQConsumer> rabbitMQConsumerAsyncResult){
        RabbitMQConsumer mqConsumer = rabbitMQConsumerAsyncResult.result();
        mqConsumer.handler(message -> {
            LOG.info("Got message: {}" ,message.body());
            double outputPrice = Double.parseDouble(message.body().toString()) * CONVERSION_RATE;
            LOG.info("Writing price {} to price-stream", outputPrice);
            priceEmitter.send(outputPrice);
        });
    }



    public void start() {
        started = true;
    }

}
