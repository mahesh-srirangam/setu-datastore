/**
 * ****************************************************************************
 *
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 *
 *
 * NOTICE: All information contained herein is, and remains the property of
 * FaEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye
 */

package com.fareyeconnect.util.email;

import java.io.Serializable;

import java.util.HashMap;

import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.opentelemetry.api.internal.StringUtils;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@ApplicationScoped
public class EmailService {

    @Inject
    @Channel("email")
    Emitter<Email> emitter;

    @ConfigProperty(name = "email.topic")
    private String emailTopic;

    public void sendEmail(Email email) {
        try {
            // if body exists add to props
            if (!StringUtils.isNullOrEmpty(email.getBody())) {
                Map<String, Serializable> props = new HashMap<>();
                props.put("info", email.getBody());
                email.setProps(props);
            }
            emitter.send(KafkaRecord.of(emailTopic, UUID.randomUUID().toString(), email));
        } catch (Exception e) {
            Log.errorf("Exception sendEmail {}", e);
        }
    }
}