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
package com.fareyeconnect.util.log;

import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 03-Dec-2022, 7:03:58 PM
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TrafficLog {

    private String id;
    private String orgId;
    private String serviceCode;
    private String url;
    private MultivaluedMap<String, String> headers;
    private MultivaluedMap<String, String> responseHeaders;
    private MultivaluedMap<String, String> queryString;
    private String requestMethod;
    private Long requestTime;
    private String requestBody;
    private Long responseTime;
    private String responseBody;
    private Integer statusCode;
    private String exception;
    private String carrierCode;
    private String clientIp;
    private String traceId;
    private boolean outbound;
    private Long executionTime;

}