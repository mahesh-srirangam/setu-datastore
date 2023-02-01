package com.fareyeconnect.service;

import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.controller.AmazonSQSConfiguration;
import com.fareyeconnect.controller.KafkaConfiguration;
import com.fareyeconnect.controller.RabbitMQConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author srirangam
 * @since 18/01/23 9:50 pm
 */

@ApplicationScoped
public class MessagingQueueFactory {

    @Inject
    KafkaConfiguration kafkaConfiguration;

    @Inject
    RabbitMQConfiguration rabbitMQConfiguration;

    @Inject
    AmazonSQSConfiguration amazonSQSConfiguration;

    public MessagingQueue getMessagingQueue(AppConstant.MessageQueue msgQueue){
        switch (msgQueue){
            case KAFKA:
                return kafkaConfiguration;
            case RABBITMQ:
                return rabbitMQConfiguration;
            case AMAZONSQS:
                return amazonSQSConfiguration;
        }
        return null;
    }

}
