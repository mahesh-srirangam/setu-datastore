package com.fareyeconnect.tool.controller;


import com.fareyeconnect.tool.service.FlowExecutionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jboss.resteasy.reactive.RestPath;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path("/app")
public class ServiceExecutorController {

    @Inject
    FlowExecutionService flowExecutionService;

    @Path("/rest/{version}/{connectorCode}/{serviceCode}")
    public void serviceExecution(@RestPath String connectorCode, @RestPath String serviceCode, String request) throws JsonProcessingException {
        flowExecutionService.initiateFlowExecution(connectorCode, serviceCode, request);
    }
}
