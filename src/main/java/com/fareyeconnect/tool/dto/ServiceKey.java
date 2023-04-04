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

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Hemanth Reddy
 * @since 31/12/22
 */
@Data
@AllArgsConstructor
@Builder
@RegisterForReflection
public class ServiceKey {

    private String connector;
    private String service;
    private String createdByOrg;
    private int connectorVersion;
}
