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
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
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
public class KafkaQueueConsumer {
    @Inject
    KafkaConfiguration kafkaConfig;

    private static final double CONVERSION_RATE = .88;

    private boolean started = false;

    private KafkaConsumer<String, Integer> priceConsumer;

    /**
     * Consuming messages from a single topic
     */
    void init() {
        Log.info("Initializing Kafka Consumer");
        priceConsumer = kafkaConfig.getConsumer();
        priceConsumer.subscribe(kafkaConfig.getTopic(), ar -> {
            if (ar.succeeded()) {
                Log.info("Successfully subsribed to topic {}"+ kafkaConfig.getTopic());
            } else {
                Log.error("Could not subscribe {}"+ ar.cause().getMessage());
            }
        }).handler(this::consumeMessage);
    }

    /**
     * Consuming messages from list of topics
     */
    void initialise(){
        priceConsumer = kafkaConfig.getConsumer();
        //Subscribe to list of topics of kafka consumer
        priceConsumer.subscribe(kafkaConfig.getListOfTopics(), ar -> {
            if (ar.succeeded()) {
                Log.info("Successfully subsribed to topic {}"+ kafkaConfig.getTopic());
            } else {
                Log.error("Could not subscribe {}"+ ar.cause().getMessage());
            }
        }).handler(this::consumeMessage);
    }

    /**
     * Consuming the message received from kafka
     * @param kafkaRecord
     */
    private void consumeMessage(KafkaConsumerRecord<String, Integer>  kafkaRecord){
        int inputPrice = kafkaRecord.value();
        Log.info("Read price {} from Kafka"+ inputPrice);
        if (started) {
            double outputPrice = inputPrice * CONVERSION_RATE;
            Log.info("Writing price {} to price-stream"+ outputPrice);
        }
    }

    public void start() {
        started = true;
    }

}
