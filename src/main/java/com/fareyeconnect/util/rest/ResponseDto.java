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
package com.fareyeconnect.util.rest;

import javax.ws.rs.core.MultivaluedMap;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseDto {

    private Integer statusCode;
    private String response;
    private Long timestamp;
    private MultivaluedMap<String, String> header;

    public ResponseDto(Integer statusCode, String response, Long timestamp) {
        this.statusCode = statusCode;
        this.response = response;
        this.timestamp = timestamp;
    }

}