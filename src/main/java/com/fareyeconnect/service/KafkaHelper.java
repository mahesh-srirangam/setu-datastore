package com.fareyeconnect.service;

import com.fareyeconnect.config.Property;
import com.fareyeconnect.tool.Helper;
import com.fareyeconnect.util.email.Email;
import com.fareyeconnect.util.email.EmailService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class KafkaHelper {

    @Inject
    private static Property property;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    EmailService emailService;

    @Inject
    @Channel("test")
    Emitter<Object> kafkaEmitter;


    public void sendBatch(String keyField, List<Object> messages, String topic) {
        messages.forEach(message -> {
            try {
                JsonNode object = objectMapper.readValue((JsonParser) message, JsonNode.class);
                pushToKafka(object.has(keyField) ? object.get(keyField).asText() : UUID.randomUUID().toString(), message, topic);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void send(String key, Object message, String topic) {
        try {
            pushToKafka(key, message, topic);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Helper(description = "Send mails to kafka topic of integration cluster.Required fields(to,subject,props{info})")
    public void sendEmail(String emailDtoStr) throws JsonProcessingException {
        Email email = objectMapper.readValue(emailDtoStr, Email.class);
        emailService.sendEmail(email);
    }

    @Helper(description = "Send messages to kafka topic of integration cluster.")
    public void sendJson(List<String> keyList, List<String> messageList, String topic) throws JsonProcessingException {
        for (int i = 0; i < keyList.size(); i++) {
            Map<String, Object> object = objectMapper.readValue(messageList.get(i), new TypeReference<Map<String, Object>>() {
            });
            String key = keyList.get(i);
            if (key == null || "".equalsIgnoreCase(key)) key = UUID.randomUUID().toString();

        }
    }

    public void pushToKafka(String key, Object message, String topic) {
        kafkaEmitter.send(KafkaRecord.of(topic, null != key ? key : UUID.randomUUID().toString(), message));
    }

}
