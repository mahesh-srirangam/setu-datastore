/*
 * *
 *  * ****************************************************************************
 *  *
 *  * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 *  * reserved.
 *  * ___________________________________________________________________________________
 *  *
 *  *
 *  * NOTICE: All information contained herein is, and remains the property of
 *  * FaEye and its suppliers,if any. The intellectual and technical concepts
 *  * contained herein are proprietary to FarEye. and its suppliers and
 *  * may be covered by us and Foreign Patents, patents in process, and are
 *  * protected by trade secret or copyright law. Dissemination of this information
 *  * or reproduction of this material is strictly forbidden unless prior written
 *  * permission is obtained from FarEye
 *
 */

package com.fareyeconnect.tool.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author Hemanth Reddy
 * @since 31/12/22
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class XmlSchema {
    private String name;
    private String header;
    private String rootElement;
    private boolean isMainSchema;
    private String schema;

    public boolean getIsMainSchema() {
        return isMainSchema;
    }

    public void setIsMainSchema(boolean mainSchema) {
        isMainSchema = mainSchema;
    }
}
