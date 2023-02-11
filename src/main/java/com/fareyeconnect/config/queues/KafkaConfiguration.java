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
import com.fareyeconnect.tool.dto.Config;
import com.vladmihalcea.hibernate.util.StringUtils;
import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author srirangam
 * @since 30/12/22 9:50 am
 */

@ApplicationScoped
public class KafkaConfiguration implements MessagingQueue {

    @Inject
    Vertx vertx;

    @Inject
    KafkaQueueConsumer kafkaQueueConsumer;

    private KafkaConsumer<String, Integer> consumer;

    private String kafkaTopic;
    /**
     * Initialise the Kafka Queue with configuration
     */
    @Override
    public void init(Config config) {
        Log.info("Kafka Launched in the kart");
        kafkaTopic = config.getKafkaTopic();
        consumer = KafkaConsumer.create(vertx, consumerConfig(config));
        kafkaQueueConsumer.init(consumer, kafkaTopic);
    }

    public KafkaConsumer<String, Integer> getConsumer() {
        return consumer;
    }

    /**
     * Get the kafka topic name(in case of single topic)
     * @return
     */
    public String getTopic() {
        return kafkaTopic;
    }

    /**
     * Get the list of topics(in case of multiple topics)
     * @return
     */
    public Set<String> getListOfTopics(){
        return StringUtils.isBlank(kafkaTopic)?Collections.emptySet(): Set.of(kafkaTopic.split(","));
    }

    /**
     * Consumer configuration for kafka
     * @return
     */
    private Map<String, String> consumerConfig(Config config) {
        Map<String, String> consumerConfig = new HashMap<>();
        consumerConfig.put("bootstrap.servers", config.getBootstrapServer() );
        consumerConfig.put("key.deserializer", config.getKeyDeserializer());
        consumerConfig.put("value.deserializer", config.getValueDeserializer());
        consumerConfig.put("group.id", config.getKafkaTopic()+"-consumer-group");
        return consumerConfig;
    }

    @PreDestroy
    void close() {
        if(consumer!=null)consumer.close();
    }
}
