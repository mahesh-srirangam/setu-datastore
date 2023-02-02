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
import com.vladmihalcea.hibernate.util.StringUtils;
import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;


/**
 * @author srirangam
 * @since 30/12/22 9:50 am
 */

@ApplicationScoped
public class KafkaConfiguration implements MessagingQueue {

    @Inject
    @ConfigProperty(name = "kafka.bootstrap.server")
    String bootstrapServer;

    @Inject
    Vertx vertx;

    @Inject
    @ConfigProperty(name = "kafka.topic")
    String topic;

    @Inject
    KafkaQueueConsumer kafkaQueueConsumer;

    private KafkaConsumer<String, Integer> consumer;
    private KafkaProducer<String, Integer> producer;

    /**
     * Initialise the Kafka Queue with configuration
     */
    @Override
    public void init() {
        Log.info("Kafka Launched in the kart");
        consumer = KafkaConsumer.create(vertx, consumerConfig());
        kafkaQueueConsumer.init();
    }

    public KafkaConsumer<String, Integer> getConsumer() {
        return consumer;
    }

    public KafkaProducer<String, Integer> getProducer() {
        return producer;
    }

    /**
     * Get the kafka topic name(in case of single topic)
     * @return
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Get the list of topics(in case of multiple topics)
     * @return
     */
    public Set<String> getListOfTopics(){
        return StringUtils.isBlank(topic)?Collections.emptySet(): Set.of(topic.split(","));
    }

    /**
     * Producer configuration for kafka
     * @return
     */
    private Map<String, String> producerConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapServer);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        return config;
    }

    /**
     * Consumer configuration for kafka
     * @return
     */
    private Map<String, String> consumerConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapServer);
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        config.put("group.id", "vertx_consumer");
        return config;
    }

    @PreDestroy
    void close() {
        if(consumer!=null)consumer.close();
        if(producer!=null)producer.close();
    }
}
