package com.fareyeconnect.controller;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author srirangam
 * @since 30/12/22 10:11 am
 */

@ApplicationScoped
public class AmazonSQSQueueConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSQueueConsumer.class);
    private static final double CONVERSION_RATE = .88;
    @Inject
    AmazonSQSConfiguration amazonSQSConfiguration;
    @Inject
    @Channel("price-stream")
    Emitter<Double> priceEmitter;
    private SqsClient sqsClient;

    /**
     * Consuming the messages from the amazon sqs queue
     */
    void init() {
        LOG.info("Initializing AmazonSQS Consumer");
        sqsClient = amazonSQSConfiguration.getConsumer();
        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(amazonSQSConfiguration.getQueue())
                .build();

        String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
        ReceiveMessageRequest receiveReq = ReceiveMessageRequest.builder()
                .maxNumberOfMessages(5)
                .queueUrl(queueUrl)
                .build();
        try {
            consumeMessage(receiveReq);
        } catch (Exception ex) {
            LOG.info("Failed to connect to Amazon SQS: {}" , ex.getMessage());
        }
    }

    /**
     * Consuming the message received from amazonSQS queue
     *
     * @param
     * @param receiveReq
     */
    private void consumeMessage(ReceiveMessageRequest receiveReq) {
        sqsClient.receiveMessage(receiveReq).messages().forEach(message -> {
            LOG.info("Got message: {}", message.body());
            double outputPrice = Double.parseDouble(message.body()) * CONVERSION_RATE;
            LOG.info("Writing price {} to price-stream", outputPrice);
            priceEmitter.send(outputPrice);
        });
    }

}
