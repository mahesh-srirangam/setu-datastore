package com.fareyeconnect.tool.service;

import com.fareyeconnect.exception.AppException;
import com.fareyeconnect.tool.model.DeployedService;
import com.fareyeconnect.tool.model.Service;
import com.fareyeconnect.tool.task.TaskList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FlowExecutionService {

    @Inject
    ConnectorService connectorService;

    @Inject
    ObjectMapper objectMapper;

    public void initiateFlowExecution(String connectorCode, String serviceCode, String request) throws JsonProcessingException {
        Service service = objectMapper.readValue("",Service.class);
        String language = "js";
        try (Context context = buildContext(language, request)) {

        } catch (Exception e) {
            throw new AppException("Flow execution failed with error " + e.getMessage());
        }
    }

    private Context buildContext(String language, String request) {
        Context context = Context.newBuilder()
                .allowAllAccess(true)
                .build();
        Value languageBindings = context.getBindings(language);
        languageBindings.putMember("request", request);
        languageBindings.putMember("PROPERTY", "");
        languageBindings.putMember("VARIABLE", "");
        return context;
    }
}
