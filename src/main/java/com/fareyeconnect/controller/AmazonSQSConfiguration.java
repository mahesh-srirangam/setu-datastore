package com.fareyeconnect.controller;


import com.fareyeconnect.service.MessagingQueue;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import com.vladmihalcea.hibernate.util.StringUtils;
import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logmanager.handlers.AsyncHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author srirangam
 * @since 30/12/22 9:50 am
 */

@ApplicationScoped
public class AmazonSQSConfiguration implements MessagingQueue {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSConfiguration.class);
    @Inject
    @ConfigProperty(name = "amazonsqs.queue")
    String queue;

    private SqsClient sqsCLient;
    @Inject
    @ConfigProperty(name = "aws_access_key_id")
    private String accessKeyId;

    @Inject
    @ConfigProperty(name = "aws_secret_access_key")
    private String secretKey;

    @Inject
    AmazonSQSQueueConsumer amazonSQSQueueConsumer;


    /**
     * Initialise the AmazonSQS Queue with configuration
     */
    @Override
    public void init() {
        LOG.info("Amazon-SQS getting initialised");
        sqsCLient = sqsClientConfig();
        amazonSQSQueueConsumer.init();
    }

    public String getQueue() {
        return queue;
    }

    public Set<String> getListOfQueues(){
        return StringUtils.isBlank(queue)? Collections.emptySet(): Set.of(queue.split(","));
    }

    private SqsClient sqsClientConfig(){
        return SqsClient.builder().region(Region.US_EAST_1).
                credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId,secretKey)))
                .build();
    }

    public SqsClient getConsumer() {
        return sqsCLient;
    }

    @PreDestroy
    void close() {
        if (sqsCLient != null) {
            sqsCLient.close();
        }
    }
}
