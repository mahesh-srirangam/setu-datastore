package com.fareyeconnect.config.queues;


import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.service.MessagingQueueFactory;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.ConfigValue;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @author srirangam
 * @since 26/12/22 4:10 pm
 */
@ApplicationScoped
public class BaseQueueConfiguration {
    private static final Logger LOGGER = Logger.getLogger(BaseQueueConfiguration.class.getName());

    @Inject
    MessagingQueueFactory messagingQueueFactory;
    void onStartUp(@Observes StartupEvent start){
        LOGGER.info("onStart event");
    }

    /**
     * Initialise the type of messaging queue at runtime
     * RabbitMQ, Kafka
     */
    @PostConstruct
    public void messagingQueueInit(){
        LOGGER.info("Messaging queue intialisation");
        ConfigValue s = ConfigProvider.getConfig().getConfigValue("implement.queue");
        messagingQueueFactory.getMessagingQueue(AppConstant.MessageQueue.valueOf(s.getValue().toUpperCase())).init();
    }

}
