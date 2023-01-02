package com.fareyeconnect.tool.controller;


import com.fareyeconnect.tool.model.Service;
import com.fareyeconnect.tool.service.ConnectorService;
import com.fareyeconnect.tool.service.FlowExecutionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.xml.bind.JAXBException;
import org.jboss.resteasy.reactive.RestPath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.stream.XMLStreamException;
import java.util.concurrent.ExecutionException;


@Path("/")
public class ServiceExecutorController {

    @Inject
    FlowExecutionService flowExecutionService;

    @Inject
    ConnectorService connectorService;

    @Path("{version}/{connectorCode}/{serviceCode}")
    @POST
    public void serviceExecution(@RestPath String connectorCode, @RestPath String serviceCode, String request) throws JsonProcessingException, XMLStreamException, ClassNotFoundException, JAXBException {
        flowExecutionService.initiateFlowExecution(connectorCode, serviceCode, request);
    }

    @Path("publish")
    @POST
    public void test(Service service){
        connectorService.putService(service);
    }
}
