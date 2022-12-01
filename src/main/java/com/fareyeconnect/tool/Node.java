package com.fareyeconnect.tool;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

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