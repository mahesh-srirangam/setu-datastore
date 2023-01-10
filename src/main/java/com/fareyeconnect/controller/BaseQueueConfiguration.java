package com.fareyeconnect.controller;



import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.ConfigValue;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

/**
 * @author srirangam
 * @since 26/12/22 4:10 pm
 */
@ApplicationScoped
public class BaseQueueConfiguration {
    private static final Logger LOGGER = Logger.getLogger(BaseQueueConfiguration.class.getName());

    @Inject
    KafkaConfiguration kafkaConfiguration;

    @Inject
    RabbitMQConfiguration rabbitMQConfiguration;


    void onStartUp(@Observes StartupEvent start){
        LOGGER.info("onStart missing");
    }

    @PostConstruct
    public void postCOndt(){
        LOGGER.info("STarted in the kart");
            ConfigValue s = ConfigProvider.getConfig().getConfigValue("implement.queue");
        if(s.getValue()!=null && s.getValue().equalsIgnoreCase("kafka")){
            kafkaConfiguration.init();
        }

        if(s.getValue()!=null && s.getValue().equalsIgnoreCase("rabbitmq")){
            rabbitMQConfiguration.init();
        }

        try {
            String value = CDI.current().select(KafkaTest.class).get().toString();
            LOGGER.info("value for KafkaTest::"+value);
        }catch (UnsatisfiedResolutionException ex){
            LOGGER.info("No class found");
            LOGGER.info(ex.getClass());
        }

    }

}
