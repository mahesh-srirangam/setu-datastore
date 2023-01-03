package com.fareyeconnect.controller;

import io.vertx.kafka.client.consumer.KafkaConsumer;
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
public class PriceConverter {
    private static final Logger LOG = LoggerFactory.getLogger(PriceConverter.class);

    @Inject
    KafkaConfiguration kafkaConfig;

    @Inject
    @Channel("price-stream")
    Emitter<Double> priceEmitter;

    private static final double CONVERSION_RATE = .88;

    private boolean started = false;

    private KafkaConsumer<String, Integer> priceConsumer;

    @PostConstruct
    void init() {
        LOG.info("Initializing PriceConverter");
        priceConsumer = kafkaConfig.getConsumer();
        priceConsumer.subscribe(kafkaConfig.getTopic(), ar -> {
            if (ar.succeeded()) {
                LOG.info("Successfully subsribed to topic {}", kafkaConfig.getTopic());
            } else {
                LOG.error("Could not subscribe {}", ar.cause().getMessage());
            }
        }).handler(kafkaRecord -> {
            int inputPrice = kafkaRecord.value();
            LOG.info("Read price {} from Kafka", inputPrice);
            if (started) {
                double outputPrice = inputPrice * CONVERSION_RATE;
                LOG.info("Writing price {} to price-stream", outputPrice);
                priceEmitter.send(outputPrice);
            }
        });
    }

    void initialise(){
        priceConsumer = kafkaConfig.getConsumer();
        //Subscribe to list of topics of kafka consumer
        priceConsumer.subscribe(kafkaConfig.getListOfTopics(), ar -> {
            if (ar.succeeded()) {
                LOG.info("Successfully subsribed to topic {}", kafkaConfig.getTopic());
            } else {
                LOG.error("Could not subscribe {}", ar.cause().getMessage());
            }
        }).handler(kafkaRecord -> {
            int inputPrice = kafkaRecord.value();
            LOG.info("Read price {} from Kafka", inputPrice);
            if (started) {
                double outputPrice = inputPrice * CONVERSION_RATE;
                LOG.info("Writing price {} to price-stream", outputPrice);
                priceEmitter.send(outputPrice);
            }
        });
    }

    public void start() {
        started = true;
    }

}
