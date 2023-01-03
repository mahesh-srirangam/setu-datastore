package com.fareyeconnect.controller;

import io.quarkus.arc.Unremovable;
import io.smallrye.common.annotation.Identifier;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * @author srirangam
 * @since 26/12/22 8:29 am
 */


@ApplicationScoped
@Unremovable
public class KafkaTest {

    @Produces
    @Identifier("default-kafka-broker")
    public Map<String, String> createKakfaRuntimeConfig(){
        Map<String,String> properties = new HashMap<>();
        System.out.println("Testing bingo:::I am starting");
        StreamSupport.stream(ConfigProvider.getConfig().getPropertyNames().spliterator(), false)
                        .map(String::toLowerCase).filter(ele->ele.startsWith("kafka")).distinct().sorted()
                        .forEach(ele->{
                            final String key = ele.substring("kafka".length() + 1).toLowerCase().replaceAll("[^a-z0-9.]", ".");
                            final String value = ConfigProvider.getConfig().getOptionalValue(ele, String.class).orElse("");
                            properties.put(key, value);
                        });
        properties.entrySet().forEach(System.out::println);
        System.out.println(ConfigProvider.getConfig().getConfigValue("mp.messaging.incoming.accounts-in.connector"));

        return properties;
    }



}
