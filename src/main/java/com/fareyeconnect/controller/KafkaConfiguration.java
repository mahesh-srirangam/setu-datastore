package com.fareyeconnect.controller;

import com.vladmihalcea.hibernate.util.StringUtils;
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
public class KafkaConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConfiguration.class);

    @Inject
    @ConfigProperty(name = "kafka.bootstrap.server")
    String bootstrapServer;

    @Inject
    Vertx vertx;

    @Inject
    @ConfigProperty(name = "kafka.topic")
    String topic;


    private KafkaConsumer<String, Integer> consumer;
    private KafkaProducer<String, Integer> producer;

    void init() {
        LOG.info("Kafka Launched in the kart");
        consumer = KafkaConsumer.create(vertx, consumerConfig());
        producer = KafkaProducer.create(vertx, producerConfig());
    }

    public KafkaConsumer<String, Integer> getConsumer() {
        return consumer;
    }

    public KafkaProducer<String, Integer> getProducer() {
        return producer;
    }

    public String getTopic() {
        return topic;
    }

    public Set<String> getListOfTopics(){
        return StringUtils.isBlank(topic)?Collections.emptySet(): Set.of(topic.split(","));
    }

    private Map<String, String> producerConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapServer);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        return config;
    }

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
