package com.fareyeconnect.controller;


import com.fareyeconnect.service.MessagingQueue;
import com.vladmihalcea.hibernate.util.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
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
