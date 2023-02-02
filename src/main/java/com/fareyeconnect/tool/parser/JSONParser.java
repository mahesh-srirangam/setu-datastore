package com.fareyeconnect.tool.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.Startup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Startup
public class JSONParser implements Parser {

    @Inject
    ObjectMapper objectMapper;

    @Override
    public Object parse(String schema, String requestBody) throws JsonProcessingException {
        //validate schema
        return objectMapper.readTree(requestBody);
    }
}
