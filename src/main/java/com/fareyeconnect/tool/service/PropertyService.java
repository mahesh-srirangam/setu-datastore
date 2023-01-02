package com.fareyeconnect.tool.service;

import com.fareyeconnect.util.rest.RestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import lombok.Data;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.LinkedHashMap;

//@Startup
@Data
@ApplicationScoped
public class PropertyService {
    @Inject
    RestService restService;

    @Inject
    ObjectMapper objectMapper;

    LinkedHashMap<String, Object> propertyMap;

    //@ConfigProperty(name = "property.url")
    private String configUrl;

    void onStart(@Observes StartupEvent ev) throws JsonProcessingException {
        Log.info("Fetching from property server ");
//        propertyMap = new LinkedHashMap<>();
//        ResponseDto responseDto = restService.invoke(configUrl, HttpMethod.POST, null, null, null, 30);
//        propertyMap = objectMapper.readValue(responseDto.getResponse(), new TypeReference<>() {});
    }
}
