package com.fareyeconnect.tool.controller;


import com.fareyeconnect.tool.service.FlowExecutionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jboss.resteasy.reactive.RestPath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.concurrent.ExecutionException;

@Path("/")
public class ServiceExecutorController {

    @Inject
    FlowExecutionService flowExecutionService;

    @Path("{version}/{connectorCode}/{serviceCode}")
    @POST
    public void serviceExecution(@RestPath String connectorCode, @RestPath String serviceCode, String request) throws JsonProcessingException, ExecutionException, InterruptedException {
        flowExecutionService.initiateFlowExecution(connectorCode, serviceCode, request);
    }
}
