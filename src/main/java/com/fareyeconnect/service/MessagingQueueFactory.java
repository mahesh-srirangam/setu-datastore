package com.fareyeconnect.service;

import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.config.queues.KafkaConfiguration;
import com.fareyeconnect.config.queues.RabbitMQConfiguration;

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

    public MessagingQueue getMessagingQueue(AppConstant.MessageQueue msgQueue){
        switch (msgQueue){
            case KAFKA:
                return kafkaConfiguration;
            case RABBITMQ:
                return rabbitMQConfiguration;
        }
        return null;
    }

}
