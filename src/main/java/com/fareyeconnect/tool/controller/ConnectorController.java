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

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.fareyeconnect.config.PageRequest;
import com.fareyeconnect.config.Paged;
import com.fareyeconnect.exception.AppException;
import com.fareyeconnect.service.KafkaHelper;
import com.fareyeconnect.service.UtilHelper;
import com.fareyeconnect.tool.model.Connector;
import com.fareyeconnect.tool.service.ConnectorService;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.internal.StringUtils;
import io.smallrye.mutiny.Uni;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    KafkaHelper kafkaHelper;

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

    @GET
    @Path("/helper")
    public void hepler() throws IOException {
        List<Object> msg = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("1","Hello");
        String m = objectMapper.writeValueAsString(map);
        msg.add(m);
        kafkaHelper.sendBatch(null, msg, "int-fareye-test" );
    }

}