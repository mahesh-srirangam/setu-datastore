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

package com.fareyeconnect.tool;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hemanth Reddy
 * @since 30/12/22
 */
@Data
public class Node {
    private String id;
    private String component;
    private String parentId;
    private String code;
    private List<Node> children = new ArrayList<Node>();

    public static List<Node> toTree(final List<Node> originalList) {
        final List<Node> copyList = new ArrayList<>(originalList);
        copyList.forEach(element -> {
            originalList
                    .stream()
                    .filter(parent -> parent.id.equals(element.parentId))
                    .findAny()
                    .ifPresent(parent -> {
                        if (parent.children == null) {
                            parent.children = new ArrayList<>();
                        }
                        parent.children.add(element);
                    });
        });
        originalList.subList(1, originalList.size()).clear();
        return originalList;
    }
}