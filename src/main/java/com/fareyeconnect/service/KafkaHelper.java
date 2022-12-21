package com.fareyeconnect.service;

import com.fareyeconnect.config.Property;
import com.fareyeconnect.tool.Helper;
import com.fareyeconnect.tool.dto.GpsUpdate;
import com.fareyeconnect.util.email.Email;
import com.fareyeconnect.util.email.EmailService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
@ApplicationScoped
public class KafkaHelper {

    @Inject
    private static Property property;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    EmailService emailService;

    @Inject
    EventService eventService;

    @Inject
    @Channel("test")
    Emitter<Object> kafkaEmitter;


    public void sendBatch(String keyField, List<Object> messages, String topic) {
        messages.forEach(message -> {
            try {
                JsonNode object = objectMapper.readValue((JsonParser) message, JsonNode.class);
                pushToKafka(object.has(keyField) ? object.get(keyField).asText() : UUID.randomUUID().toString(), message, topic);
            } catch (Exception e) {
                Log.error(e);
                eventService.sendEvent(messages, topic, null);
            }
        });
    }

    public void send(String key, Object message, String topic) {
        try {
            pushToKafka(key, message, topic);
        } catch (Exception e) {
            eventService.sendEvent(message, topic, null);
        }
    }

    @Helper(description = "Send mails to kafka topic of integration cluster.Required fields(to,subject,props{info})")
    public void sendEmail(String emailDtoStr) throws JsonProcessingException {
        Email email = objectMapper.readValue(emailDtoStr, Email.class);
        emailService.sendEmail(email);
    }

    @Helper(description = "Send messages to kafka topic of integration cluster.")
    public void sendJson(List<String> keyList, List<String> messageList, String topic) throws JsonProcessingException {
        try {
            for (int i = 0; i < keyList.size(); i++) {
                Map<String, Object> object = objectMapper.readValue(messageList.get(i), new TypeReference<Map<String, Object>>() {
                });
                String key = keyList.get(i);
                pushToKafka(key, object, topic);
            }
        } catch (Exception e) {
            eventService.sendEvent(messageList, topic, null);
        }
    }

    @Helper(description = "Save Message in case of kafka failure")
    public void saveMessage(List<String> messages, String topic) {
        eventService.sendEvent(messages, topic, null);
    }

    @Helper(description = "Save Message in case of kafka failure")
    public void saveMessage(List<String> messages, String topic, String serverAddress) {
        eventService.sendEvent(messages, topic, serverAddress);
    }

    @Helper(description = "Push list of gps updates to kafka.")
    public void sendGpsUpdate(List<String> updates) throws JsonProcessingException {
        String topic = property.getGpsUpdatesTopic();
        try {
            for (String message : updates) {
                GpsUpdate gpsUpdate = objectMapper.readValue(message, GpsUpdate.class);
                GpsUpdate.validate(gpsUpdate);
                String key = gpsUpdate.getVehicleNo();
                pushToKafka(key, message, topic);
            }
        } catch (Exception e) {
            eventService.sendEvent(updates, topic, null);
        }
    }

    public void pushToKafka(String key, Object message, String topic) {
        kafkaEmitter.send(KafkaRecord.of(topic, null != key ? key : UUID.randomUUID().toString(), message));
    }

}
