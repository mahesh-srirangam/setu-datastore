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

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.util.log.TrafficLogService;

import io.netty.util.CharsetUtil;
import io.vertx.core.http.HttpServerRequest;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Inject
    TrafficLogService trafficLogService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        long requestStartTime = System.nanoTime();
        requestContext.setProperty("requestStartTime", requestStartTime);
        request.body().onComplete(ar -> {
            if (!ar.failed()) {
                requestContext.setProperty("requestBody", ar.result().toString(CharsetUtil.UTF_8));
            }
        });
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        long requestStartTime =System.nanoTime();// (long) requestContext.getProperty("requestStartTime");
        long requestFinishTime = System.nanoTime();
        long duration = requestFinishTime - requestStartTime;

        String requestBody = (String) requestContext.getProperty("requestBody");
        Object responseBody = responseContext.getEntity();
        final String method = requestContext.getMethod();
        // final String path = info.getPath();
        final String address = request.remoteAddress().toString();

        // StringBuilder sb = new StringBuilder();
        // sb.append("\nUser: ").append(requestContext.getSecurityContext().getUserPrincipal() == null ? "unknown"
        //         : requestContext.getSecurityContext().getUserPrincipal());
        // sb.append("\n - Path: ").append(path);
        // sb.append("\n - Method: ").append(method);
        // sb.append("\n - IP: ").append(address);
        // sb.append("\n - Header: ").append(requestContext.getHeaders());
        // sb.append("\n - Request Body: ").append(requestBody);
        // sb.append("\n - Response Body: ").append(responseBody);
        // sb.append("\n - Request Time: ").append(requestStartTime);
        // sb.append("\n - Response Time: ").append(requestFinishTime);
        // sb.append("\n - Duration: ").append(duration);
        // Log.infof("HTTP REQUEST %s : ", sb.toString());

        trafficLogService.log(info.getRequestUri().toString(), requestBody != null ? requestBody.toString() : null,
                responseBody != null ? responseBody.toString() : null, requestStartTime, requestFinishTime,
                responseContext.getStatus(),
                AppConstant.EMPTY, address, requestContext.getHeaders(), responseContext.getStringHeaders(),
                info.getQueryParameters(),
                method, false);

    }
}