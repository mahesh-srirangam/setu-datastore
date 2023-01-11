/**
 * ****************************************************************************
 * <p>
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 * <p>
 * <p>
 * NOTICE: All information contained herein is, and remains the property of
 * FarEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye.
 */
package com.fareyeconnect.tool.service;

import com.fareyeconnect.config.PageRequest;
import com.fareyeconnect.config.Paged;
import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.tool.model.Service;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Baldeep Singh Kwatra
 * @since 24-Dec-2022, 7:03:58 PM
 */
@ApplicationScoped
@Startup
public class ServiceService {

    public Uni<?> get(String id) {
        return Service.findById(id).onItem().ifNull().failWith(EntityNotFoundException::new);
    }

    @ReactiveTransactional
    public Uni<Service> save(Service service) {
        return service.persist();
    }

    @ReactiveTransactional
    public Uni<Service> update(Service service) {
        return Panache.getSession()
                .chain(session -> session.merge(service))
                .chain(entity -> entity.persist());
    }

    public Uni<Long> remove(String ids) {
        Uni<Long> deletedCount = Service.delete("id in (?1)", Arrays.asList(ids.split(AppConstant.COMMA)));
        return deletedCount;

    }

    public Uni<Paged<Service>> findAll(PageRequest pageRequest) {
        return new Paged<Service>().toPage(Service.findAll(pageRequest.toSort()).page(pageRequest.toPage()));
    }

    public Uni<Service> findByServiceCode(String serviceCode) {
        return Service.findByCode(serviceCode);
    }

    public Uni<List<Service>> findByActive(String status) {
        return Service.findByActive(status);
    }

    public Uni<Service> findById(String serviceCode) {
        return Service.findById(serviceCode);
    }
}