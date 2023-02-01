/**
 * ****************************************************************************
 *
 * Copyright (c) 2023, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 *
 *
 * NOTICE: All information contained herein is, and remains the property of
 * FarEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye.
 */
package com.fareyeconnect.tool.controller;


import com.fareyeconnect.tool.service.ConnectorService;
import com.fareyeconnect.tool.service.FlowExecutionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.smallrye.mutiny.Uni;
import jakarta.xml.bind.JAXBException;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.stream.XMLStreamException;
import java.util.concurrent.ExecutionException;


/**
 * @author Hemanth Reddy
 * @since 16/01/23
 */
@Path("/")
public class ServiceExecutorController {

    @Inject
    FlowExecutionService flowExecutionService;

    @Inject
    ConnectorService connectorService;

    /**
     * Following method executes a service flow.
     *
     * @param connectorCode
     * @param connectorVersion
     * @param serviceCode
     * @param request
     * @return
     * @throws JsonProcessingException
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     * @throws JAXBException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Path("{connectorCode}/{connectorVersion}/{serviceCode}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<String>> serviceExecution(@RestPath String connectorCode, @RestPath int connectorVersion, @RestPath String serviceCode, String request) throws JsonProcessingException, XMLStreamException, ClassNotFoundException, JAXBException, ExecutionException, InterruptedException {
        return flowExecutionService.initiateFlowExecution(connectorCode, connectorVersion, serviceCode, request);
    }
}
