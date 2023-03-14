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
package com.fareyeconnect.controller;

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
import com.fareyeconnect.exception.AppException;
import com.fareyeconnect.model.Variable;
import com.fareyeconnect.service.VariableService;

import io.opentelemetry.api.internal.StringUtils;
import io.smallrye.mutiny.Uni;

import java.util.concurrent.ExecutionException;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 09-Dec-2022, 7:03:58 PM
 */
@Path("/variable")
public class VariableController {

    @Inject
    VariableService variableService;

    @GET
    @Path("/{id}")
    // @PreAuthorize("hasAuthority('VARIABLE_READ')")
    public Uni<?> get(@PathParam("id") String id) {
        return variableService.get(id);
    }

    @GET
    // @PreAuthorize("hasAuthority('VARIABLE_READ')")
    public Object get(@BeanParam PageRequest pageRequest)  {
    return variableService.findAll(pageRequest);
    }

    @POST
    // @PreAuthorize("hasAuthority('VARIABLE_CREATE')")
    public Uni<Variable> save(@Valid Variable variable)  {
        if (!StringUtils.isNullOrEmpty(variable.getId())) {
            throw new AppException("id was invalidly set on request.");
        }
        return variableService.save(variable);
    }

    @PUT
    // @PreAuthorize("hasAuthority('VARIABLE_UPDATE')")
    public Uni<Variable> update(@Valid Variable variable)  {
        return variableService.update(variable);
    }

    @DELETE
    @Path("/{ids}")
    // @PreAuthorize("hasAuthority('VARIABLE_DELETE')")
    public Uni<Long> remove(String ids) {
        return variableService.remove(ids);
    }

}