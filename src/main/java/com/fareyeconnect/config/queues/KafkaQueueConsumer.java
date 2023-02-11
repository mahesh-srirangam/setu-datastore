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

import javax.enterprise.context.ApplicationScoped;
import java.util.Set;

/**
 *
 * @author srirangam
 * @since 30/12/22 10:11 am
 */

@ApplicationScoped
public class KafkaQueueConsumer {

    /**
     * Consuming messages from a single topic
     */
    void init(KafkaConsumer<String, Integer> kafkaConsumer, String kafkaTopic) {
        Log.info("Initializing Kafka Consumer");
        kafkaConsumer.subscribe(kafkaTopic, ar -> {
            if (ar.succeeded()) {
                Log.info("Successfully subsribed to topic {}"+ kafkaTopic);
            } else {
                Log.error("Could not subscribe {}"+ ar.cause().getMessage());
            }
        }).handler(this::consumeMessage);
    }


    /**
     * Consuming messages from list of topics
     */
    void initialise(KafkaConsumer<String, Integer> kafkaConsumer, Set<String> listOfTopics){
        //Subscribe to list of topics of kafka consumer
        kafkaConsumer.subscribe(listOfTopics, ar -> {
            if (ar.succeeded()) {
                Log.info("Successfully subsribed to topic {}"+listOfTopics);
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
       //invoke setu service
    }

}
