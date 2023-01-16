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
import com.fareyeconnect.tool.model.Connector;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityNotFoundException;
import java.util.Arrays;

/**
 * @author Baldeep Singh Kwatra
 * @since 24-Dec-2022, 7:03:58 PM
 */
@ApplicationScoped
public class ConnectorService {

    public Uni<?> get(String id) {
        return Connector.findById(id).onItem().ifNull().failWith(EntityNotFoundException::new);
    }

    @ReactiveTransactional
    public Uni<Connector> save(Connector connector) {
        return connector.persist();
    }

    @ReactiveTransactional
    public Uni<Connector> update(Connector connector) {
        return Panache.getSession()
                .chain(session -> session.merge(connector))
                .chain(entity -> entity.persist());
    }

    public Uni<Long> remove(String ids) {
        Uni<Long> deletedCount = Connector.delete("id in (?1)", Arrays.asList(ids.split(AppConstant.COMMA)));
        return deletedCount;

    }

    @ReactiveTransactional
    public Uni<Paged<Connector>> findAll(PageRequest pageRequest) {
        return new Paged<Connector>().toPage(Connector.findAll(pageRequest.toSort()).page(pageRequest.toPage()));
        // return new Paged<Connector>(Connector.findAll(pageRequest.toSort()).page(pageRequest.toPage()).list());
    }

    public Uni<Paged<Connector>> findNew(PageRequest pageRequest) {
        PanacheQuery<Connector> query = Connector.findAll(pageRequest.toSort()).page(pageRequest.toPage());


        Paged pagination = new Paged();
        pagination.setNumber(query.page().index);
        pagination.setSize(query.page().size);

        // this.totalElements = query.count().await().atMost(Duration.ofSeconds(3));
        // this.totalPages = pageCount(totalElements, size);
        // this.content = Collections.unmodifiableList(query.list().await().atMost(Duration.ofSeconds(3)));
        // this.first = this.number == 0;
        // this.last = this.number == this.totalPages - 1;
        // this.numberOfElements = this.content.size();
        // if (page != null && page >= 1) {
        //   pagination.setPage(page);
        //   page--;
        // } else {
        //   page = 0;
        //   pagination.setPage(1);
        // }
        // if (page_size == null || page_size <= 0) {
        //   page_size = 10;
        // }
        // pagination.setPageSize(page_size);
        return query.list()
                .map(
                        b -> {
                            pagination.setNumberOfElements(b.size());
                            pagination.setContent(b);
                            // pagination.setTotalElements(query.count().map(el->{return el;}));
                            ;
                            //   pagination.setTotalCount(b.size());
                            //   LOG.info("listBrands");
                            return pagination;
                        });


        // query.=
        // .list()
        // .map(
        //     b -> {
        //       pagination.setTotalCount(b.size());
        //       LOG.info("listBrands");
        //       return Response.ok(
        //               new BrandResponseList(new Metadata("success", 200, "ok"), b, pagination))
        //           .build();
        //     });


        // return query.list().onItem(). transform(response -> {
        //     Paged<Connector> paged = new Paged<Connector>();
        //     paged.number = query.page().index;
        //     paged.size = query.page().size;
        //     // query.count().onItem().transform(count -> paged.totalElements = count);
        //     // paged.totalPages = pageCount(paged.totalElements,paged.size);
        //     paged.content = Collections.unmodifiableList(response);
        //     paged.first = paged.number == 0;
        //     paged.last = paged.number == paged.totalPages - 1;
        //     paged.numberOfElements = paged.content.size();

        //     return paged;
        // });
    }
}