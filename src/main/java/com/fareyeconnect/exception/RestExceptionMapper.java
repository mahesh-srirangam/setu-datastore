/**
 * ****************************************************************************
 * <p>
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 * <p>
 * <p>
 * NOTICE: All information contained herein is, and remains the property of
 * FaEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye
 */
package com.fareyeconnect.exception;

import com.fareyeconnect.constant.AppConstant;
import io.quarkus.logging.Log;
import io.vertx.pgclient.PgException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.persistence.PersistenceException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {

        ApiError apiError = new ApiError(Status.BAD_REQUEST);
        apiError.setMessage(e.getMessage());
        apiError.setDebugMessage(ExceptionUtils.getStackTrace(e));

        if (e instanceof AppException) {
            Response originalErrorResponse = ((AppException) e).getResponse();
            apiError.setStatus(originalErrorResponse.getStatusInfo().getStatusCode());
            apiError.setCategory(AppException.class.getName());
        } else if (e instanceof WebApplicationException) {
            Response originalErrorResponse = ((WebApplicationException) e).getResponse();
            apiError.setStatus(originalErrorResponse.getStatusInfo().getStatusCode());
        } else if (e instanceof EntityNotFoundException) {
            apiError.setReasonPhrase(Status.NOT_FOUND);
            apiError.setCategory(EntityNotFoundException.class.getSimpleName());
        } else if (e instanceof PersistenceException) {
            // use a loop to get the PgException
            for (Throwable current = e; current != null; current = current.getCause()) {
                if (current instanceof PgException) {
                    final PgException pge = (PgException) current;
                    apiError.setMessage(pge.getLocalizedMessage()
                            + AppConstant.SPACE + pge.getTable() + AppConstant.SPACE + pge.getDetail());
                    break;
                }
            }
            apiError.setCategory(PersistenceException.class.getSimpleName());
        } else {
            apiError.setReasonPhrase(Status.INTERNAL_SERVER_ERROR);
            apiError.setCategory(e.getClass().getSimpleName());
        }

        Log.errorf("%s", apiError);
        return Response.status(apiError.getReasonPhrase()).entity(apiError).build();
    }

}
