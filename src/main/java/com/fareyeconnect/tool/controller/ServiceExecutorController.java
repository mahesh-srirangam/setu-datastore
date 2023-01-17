package com.fareyeconnect.tool.controller;


import com.fareyeconnect.tool.model.Service;
import com.fareyeconnect.tool.service.ConnectorService;
import com.fareyeconnect.tool.service.FlowExecutionService;
import com.fareyeconnect.tool.service.ServicePublisherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.smallrye.mutiny.Uni;
import jakarta.xml.bind.JAXBException;
import org.jboss.resteasy.reactive.RestPath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;
import java.util.concurrent.ExecutionException;


@Path("/")
public class ServiceExecutorController {

    @Inject
    FlowExecutionService flowExecutionService;

    @Inject
    ConnectorService connectorService;

    @Path("{connectorCode}/{connectorVersion}/{serviceCode}")
    @POST
    public Uni<Response> serviceExecution(@RestPath String connectorCode,@RestPath int connectorVersion, @RestPath String serviceCode, String request) throws JsonProcessingException, XMLStreamException, ClassNotFoundException, JAXBException, ExecutionException, InterruptedException {
        return flowExecutionService.initiateFlowExecution(connectorCode,connectorVersion ,serviceCode, request);
    }
}
