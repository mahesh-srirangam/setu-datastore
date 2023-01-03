package com.fareyeconnect.controller;

import com.fareyeconnect.util.BeanUtil;
import io.smallrye.reactive.messaging.kafka.KafkaClientService;
import io.vertx.kafka.client.common.KafkaClientOptions;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.eclipse.microprofile.config.ConfigProvider;
import org.hibernate.resource.beans.container.internal.NoSuchBeanException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.CDIProvider;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author srirangam
 * @since 25/12/22 3:21 pm
 */

@Path("/test")
public class KafkaController {

    @Inject
    KafkaClientService kafka;

    @Inject
    KafkaTest kafkaTest;

    @PostConstruct
    public void postCOndt(){
        System.out.println(hello());
        try {
            CDI.current().select(KafkaTest.class).get();
        }catch (UnsatisfiedResolutionException ex){
            System.out.println("Class Not found:::");
            System.out.println(ex.getClass());
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        BeanManager beanManager = CDI.current().getBeanManager();
        int i = beanManager.getBeans(TestController.class).size();
        TestController test = CDI.current().select(TestController.class).get();
        String s = String.valueOf(ConfigProvider.getConfig().getConfigValue("mp.messaging.incoming.accounts-in.connector"));
        kafkaTest.createKakfaRuntimeConfig();

        if(test!=null){
            return "TEST" + s;
        }
        return "HELLO"+ i;
    }

    public String checkClass(){
        String test = CDI.current().select(TestingRemovableC.class).get().toString();
        return "Checking flow:"+ test;
    }


}
