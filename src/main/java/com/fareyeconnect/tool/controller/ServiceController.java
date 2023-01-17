/**
 * ****************************************************************************
 * <p>
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 * <p>
 * <p>
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
import com.fareyeconnect.tool.model.Service;
import com.fareyeconnect.tool.service.ServicePublisherService;
import com.fareyeconnect.tool.service.ServiceService;
import io.opentelemetry.api.internal.StringUtils;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Baldeep Singh Kwatra
 * @since 09-Dec-2022, 7:03:58 PM
 */
@Path("/service")
public class ServiceController {

    @Inject
    ServiceService serviceService;

    @Inject
    ServicePublisherService servicePublisherService;

    @GET
    @Path("/{id}")
    // @PreAuthorize("hasAuthority('VARIABLE_READ')")
    public Uni<?> get(@PathParam("id") String id) {
        return serviceService.get(id);
    }

    @GET
    // @PreAuthorize("hasAuthority('VARIABLE_READ')")
    public Uni<Paged<Service>> get(@BeanParam PageRequest pageRequest) {
        return serviceService.findAll(pageRequest);
    }

    @POST
    // @PreAuthorize("hasAuthority('VARIABLE_CREATE')")
    public Uni<Service> save(@Valid Service service) {
        System.out.println(service);
        if (!StringUtils.isNullOrEmpty(service.getId())) {
            throw new AppException("id was invalidly set on request.");
        }
        return serviceService.save(service);
    }

    @PUT
    // @PreAuthorize("hasAuthority('VARIABLE_UPDATE')")
    public Uni<Service> update(@Valid Service service) {
        return serviceService.update(service);
    }

    @DELETE
    @Path("/{ids}")
    // @PreAuthorize("hasAuthority('VARIABLE_DELETE')")
    public Uni<Long> remove(String ids) {
        return serviceService.remove(ids);
    }

    @POST
    @Path("/publish")
    public Uni<Object> publish(@Valid Service service) {
        return servicePublisherService.publish(service);
    }

    @GET
    @Path("/{connector}/{connectorVersion}/{code}")
    public Uni<Service> get(@RestPath String connector, @RestPath int connectorVersion, @RestPath String code) throws ExecutionException, InterruptedException {
        return servicePublisherService.fetchLiveVersion(connector,connectorVersion, code);
    }
}