package com.fareyeconnect.service;

import com.cronutils.model.field.expression.On;
import com.fareyeconnect.config.Property;
import com.fareyeconnect.config.security.GatewayUser;
import com.fareyeconnect.dto.Events;
import com.fareyeconnect.dto.StatusUpdateRequestDto;
import com.fareyeconnect.util.rest.ResponseDto;
import com.fareyeconnect.util.rest.RestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpMethod;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    @Inject
    private Property property;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    RestService restService;

    public ResponseDto sendStatusUpdateEvent(List<StatusUpdateRequestDto> statusUpdateRequestDTOList) throws JsonProcessingException {
        String topic = property.getTrackingUpdatesTopic();
        List<Events> eventsList = new ArrayList<>();
        String consumerUrl = property.getConsumerUrl();
        if (!consumerUrl.endsWith("/")) consumerUrl += "/";
        for (StatusUpdateRequestDto statusUpdateRequestDTO : statusUpdateRequestDTOList) {
            Events events = new Events();
            events.setBody(objectMapper.writeValueAsString(statusUpdateRequestDTO));
            events.setOrgId(GatewayUser.getUser().getOrganizationId());
            events.setTopic(topic);
            events.setUrl(consumerUrl + "api/tracking");
            eventsList.add(events);
        }
        return sendEvent(eventsList);

    }

    public ResponseDto sendEvent(List<Object> messages, String topic, String serverAddress) {
        List<Events> eventsList = new ArrayList<>();
        for (Object message : messages) {
            eventsList(message, eventsList, topic, serverAddress);
        }
        return sendEvent(eventsList);
    }

    public ResponseDto sendEvent(Object message, String topic, String serverAddress) {
        List<Events> eventsList = new ArrayList<>();
        eventsList(message, eventsList, topic, serverAddress);
        return sendEvent(eventsList);
    }

    public void eventsList(Object message, List<Events> eventsList, String topic, String serverAddress) {
        Events events = new Events();
        events.setBody(message);
        events.setOrgId(GatewayUser.getUser().getOrganizationId());
        events.setTopic(topic);
        events.setBootstrapServerAddress(serverAddress);
        eventsList.add(events);
    }

    public ResponseDto sendEvent(List<Events> eventsList) {
        String ffmUrl = property.getFfmUrl();
        if (!ffmUrl.endsWith("/")) ffmUrl += "/";
        MultivaluedMap<String, String> httpHeaders = new MultivaluedHashMap<>();
        httpHeaders.add("Content-Type", "application/json");
        return restService.invoke(ffmUrl + "public/events/all", HttpMethod.POST, eventsList, httpHeaders);
    }

}
