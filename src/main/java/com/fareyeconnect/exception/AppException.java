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
package com.fareyeconnect.exception;

import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.client.api.WebClientApplicationException;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 15-May-2022, 11:12:29 AM
 */

public class AppException extends WebApplicationException {


    /**
     * Constructs an instance of <code>NewException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public AppException(String msg) {
        super(msg, Response.Status.BAD_REQUEST);
    }

    public AppException(String msg,int statusCode){
        super(msg,statusCode);
    }
    
}