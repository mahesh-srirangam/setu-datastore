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
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Random;
/**
 *
 * @author srirangam
 * @since 30/12/22 10:13 am
 */

@ApplicationScoped
public class KafkaQueueProducer {

    @Inject
    KafkaConfiguration kafkaConfig;

    private KafkaProducer<String, Integer> producer;
    private Random random = new Random();

    @PostConstruct
    void init() {
        producer = kafkaConfig.getProducer();
    }

//    @Scheduled(every = "5s")
    public void generatePrice() {
        int price = random.nextInt(100);
        Log.info("Writing price {} to Kafka"+ price);
        KafkaProducerRecord<String, Integer> dataToSend = KafkaProducerRecord.create(kafkaConfig.getTopic(), price);
        producer.send(dataToSend);
    }
}
