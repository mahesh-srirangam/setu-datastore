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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;

import com.fareyeconnect.constant.AppConstant;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import lombok.Data;

@Data
public class PageRequest {

    private final String SORT_DEFAULT = "Ascending";
    private final String SORT_DESCENDING = "Descending";
    private final Map<String, String> SORT_DIRECTION = new HashMap<String, String>() {
        {
            put("asc", SORT_DEFAULT);
            put("desc", SORT_DESCENDING);
        }
    };

    @QueryParam("page")
    @DefaultValue("0")
    private int page;

    @QueryParam("size")
    @DefaultValue("20")
    private int size;

    @QueryParam("sort")
    private List<String> sort;

    public Page toPage() {
        return Page.of(this.page, this.size);
    }

    public Sort toSort() {
        Sort sorting = Sort.empty();
        for (int index = 0; index < sort.size(); index++) {
            String item = sort.get(index);
            String columnDirection[] = item.split(AppConstant.COMMA);
            String direction = SORT_DIRECTION.get(columnDirection[1]);
            direction = StringUtils.isEmpty(direction) ? SORT_DEFAULT : direction;
            sorting = index == 0 ? Sort.by(columnDirection[0], Direction.valueOf(direction))
                    : sorting.and(columnDirection[0], Direction.valueOf(direction));
        }
        return sorting;
    }
}