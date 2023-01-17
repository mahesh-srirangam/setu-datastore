/**
 * ****************************************************************************
 * <p>
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 * <p>
 * <p>
 * NOTICE: All information contained herein is, and remains the property of
 * FarEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye.
 */
package com.fareyeconnect.tool.service;

import com.fareyeconnect.util.rest.RestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import lombok.Data;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.LinkedHashMap;


/**
 * @author Hemanth Reddy
 * @since 30/12/22
 */
@Startup
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
