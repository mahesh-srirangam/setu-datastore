/**
 * ****************************************************************************
 *
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 *
 *
 * NOTICE: All information contained herein is, and remains the property of
 * FaEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye
 */
package com.fareyeconnect.tool.controller;

import com.fareyeconnect.config.PageRequest;
import com.fareyeconnect.config.Paged;
import com.fareyeconnect.exception.AppException;
import com.fareyeconnect.service.ImageHelper;
import com.fareyeconnect.tool.model.Connector;
import com.fareyeconnect.tool.service.ConnectorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.internal.StringUtils;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 09-Dec-2022, 7:03:58 PM
 */
@Path("/connector")
public class ConnectorController {

    @Inject
    ConnectorService connectorService;

    @Inject
    ImageHelper imageHelper;

    @Inject
    ObjectMapper objectMapper;

    @GET
    @Path("/{id}")
    // @PreAuthorize("hasAuthority('VARIABLE_READ')")
    public Uni<?> get(@PathParam("id") String id) {
        return connectorService.get(id);
    }

    @GET
    // @PreAuthorize("hasAuthority('VARIABLE_READ')")
    public Uni<Paged<Connector>> get(@BeanParam PageRequest pageRequest)  {
    return connectorService.findAll(pageRequest);
    }

    @POST
    // @PreAuthorize("hasAuthority('VARIABLE_CREATE')")
    public Uni<Connector> save(@Valid Connector connector) {
        if (!StringUtils.isNullOrEmpty(connector.getId())) {
            throw new AppException("id was invalidly set on request.");
        }
        return connectorService.save(connector);
    }

    @PUT
    // @PreAuthorize("hasAuthority('VARIABLE_UPDATE')")
    public Uni<Connector> update(@Valid Connector connector) {
        return connectorService.update(connector);
    }

    @DELETE
    @Path("/{ids}")
    // @PreAuthorize("hasAuthority('VARIABLE_DELETE')")
    public Uni<Long> remove(String ids) {
        return connectorService.remove(ids);
    }

//    @GET
//    @Path("/grpc")
//    public Uni<String> hepler() throws IOException {
//        return imageHelper.grpc("https://plus.unsplash.com/premium_photo-1669559809593-077547e9c2a7?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHw3fHx8ZW58MHx8fHw%3D&auto=format&fit=crop&w=500&q=60", "abcd");
//    }
//
//    @GET
//    @Path("/rest")
//    public String grpc() throws IOException {
//        return imageHelper.rest("https://plus.unsplash.com/premium_photo-1669559809593-077547e9c2a7?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHw3fHx8ZW58MHx8fHw%3D&auto=format&fit=crop&w=500&q=60", "abcd");
//    }

}