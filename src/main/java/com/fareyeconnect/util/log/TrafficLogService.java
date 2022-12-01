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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.fareyeconnect.config.security.GatewayUser;
import com.fareyeconnect.constant.AppConstant.ServiceCode;
import io.opentelemetry.api.trace.Span;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class TrafficLogService {

    @Inject
    @Channel("traffic-log")
    Emitter<TrafficLog> emitter;

    @ConfigProperty(name = "traffic.log.topic")
    private String trafficLogTopic;

    public void log(String url, String requestData, String responseData, Long requestTime, Long responseTime,
            int statusCode, String exception, String ipAddress, MultivaluedMap<String, String> requestHeaders,
            MultivaluedMap<String, String> responseHeaders,
            MultivaluedMap<String, String> queryParams, String requestMethod, boolean outbound) {
        ServiceCode serviceCode = ServiceDetail.getServiceCode();// set by dev or from url
        TrafficLog trafficLogs = TrafficLog.builder().carrierCode("")
                .orgId(GatewayUser.getUser() == null ? "1" : GatewayUser.getUser().getOrganizationId())
                .clientIp(ipAddress).url(url).headers((MultivaluedMap<String, String>) requestHeaders)
                .responseHeaders(responseHeaders)
                .exception(exception).queryString(queryParams).responseTime(responseTime).statusCode(statusCode)
                .responseBody(responseData).requestTime(requestTime).requestMethod(requestMethod)
                .requestBody(requestData).serviceCode(serviceCode != null ? serviceCode.toString() : "")
                .traceId(getTraceId()).outbound(outbound).build();

        trafficLogs.setCarrierCode(ServiceDetail.getCarrierCode());
        insertTrafficLogs(trafficLogs);
    }

    /**
     * Insert into traffic logs table.
     *
     * @param trafficLogs
     */
    public void insertTrafficLogs(TrafficLog trafficLog) {
        try {
            pushLogsToTopic(trafficLog);
        } catch (Exception e) {
            Log.errorf("Exception insertTrafficLogs {}", e);
        }
    }

    public void pushLogsToTopic(TrafficLog trafficLog) {
        try {
            emitter.send(KafkaRecord.of(trafficLogTopic, UUID.randomUUID().toString(), trafficLog));
        } catch (Exception e) {
            Log.errorf("Exception pushLogsToTopic {}", e);
        }
    }

    /**
     * Get Trace Id of current request
     * 
     * @return
     */
    private String getTraceId() {
        return Span.current().getSpanContext().getTraceId();
    }
}