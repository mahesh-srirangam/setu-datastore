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
package com.fareyeconnect.tool.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.graalvm.polyglot.Context;

import java.io.Serializable;
import java.util.List;

/**
 * @author Hemanth Reddy
 * @since 30/12/22
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RestCall.class, name = "Rest"),
        @JsonSubTypes.Type(value = Start.class, name = "Start"),
        @JsonSubTypes.Type(value = SystemTask.class, name = "SystemTask"),
        @JsonSubTypes.Type(value = FileService.class, name = "FileServices"),
})
@Data
public abstract class Task implements Serializable {

    private String taskNumber;

    private String type;
    private List<String> nextTask;


    public abstract void execute(Context context);
}
