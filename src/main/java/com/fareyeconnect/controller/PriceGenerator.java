package com.fareyeconnect.controller;

import io.quarkus.scheduler.Scheduled;
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
public class PriceGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(PriceGenerator.class);

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
        LOG.info("Writing price {} to Kafka", price);
        KafkaProducerRecord<String, Integer> dataToSend = KafkaProducerRecord.create(kafkaConfig.getTopic(), price);
        producer.send(dataToSend);
    }
}
