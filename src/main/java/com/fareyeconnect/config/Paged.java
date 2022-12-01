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
package com.fareyeconnect.config;

import java.util.Collections;
import java.util.List;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.smallrye.mutiny.Uni;
import lombok.Data;

@Data
public class Paged<T> {

    public int number;
    public int size;
    public long totalElements;
    public int totalPages;
    public boolean first;
    public boolean last;
    public long numberOfElements;
    public List<T> content;

    public Uni<Paged<T>> toPage(PanacheQuery<T> query) {
        this.number = query.page().index;
        this.size = query.page().size;
        this.first = this.number == 0;
        Uni<List<T>> list = query.list();
        return list.chain(results -> query.count().map(count -> {
            this.content = Collections.unmodifiableList(results);
            this.totalElements = count;
            this.totalPages = pageCount(totalElements, size);
            this.numberOfElements = this.content.size();
            this.last = this.number == this.totalPages - 1;
            return this;
        }));
    }

    private static int pageCount(long count, int pageSize) {
        if (count == 0)
            return 1; // a single page of zero results
        return (int) Math.ceil((double) count / (double) pageSize);
    }
}