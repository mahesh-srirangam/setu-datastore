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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.fareyeconnect.constant.AppConstant;
import com.fareyeconnect.util.log.TrafficLogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.handler.codec.http.HttpMethod;
import io.quarkus.logging.Log;

/**
 * Rest Services to invoke third party endpoints
 *
 * @author Baldeep Singh Kwatra
 * @since 18-Feb-2022, 5:38:50 PM
 */
@Singleton
// @Log4j2
// @ConditionalOnProperty(name = "enable.service.rest", havingValue = "true")
public class RestService {

    @Inject
    TrafficLogService trafficLogService;

    @Inject
    ObjectMapper objectMapper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final HttpClient httpClient = HttpClient.newBuilder()
            .executor(executorService)
            .connectTimeout(Duration.ofSeconds(5))
            .version(HttpClient.Version.HTTP_2)
            .build();

    /**
     * Invokes a GET Request to the endpoint
     *
     * @param endpoint
     * @return
     */
    public ResponseDto invoke(String endpoint) {
        return invoke(endpoint, HttpMethod.GET, null, null, null,
                AppConstant.HTTP_READ_TIMEOUT);
    }

    /**
     * Invokes a GET Request to the endpoint withing desired timeout
     *
     * @param endpoint
     * @param readTimeout
     * @return
     */
    public ResponseDto invoke(String endpoint, int readTimeout) {
        return invoke(endpoint, HttpMethod.GET, null, null, null, readTimeout);
    }

    /**
     * Invokes a GET Request to the endpoint along with Request Body
     *
     * @param endpoint
     * @param requestBody
     * @return
     */
    public ResponseDto invoke(String endpoint, Object requestBody) {
        return invoke(endpoint, HttpMethod.GET, requestBody, null, null, AppConstant.HTTP_READ_TIMEOUT);
    }

    /**
     * Make a GET Request along with Headers
     * 
     * @param endpoint
     * @param headers
     * @return
     */
    public ResponseDto invoke(String endpoint, MultivaluedMap<String, String> headers) {
        return invoke(endpoint, HttpMethod.GET, null, headers, null, AppConstant.HTTP_READ_TIMEOUT);
    }

    /**
     * Make a request along with headers and method
     * 
     * @param endpoint
     * @param headers
     * @return
     */
    public ResponseDto invoke(String endpoint, HttpMethod method, MultivaluedMap<String, String> headers) {
        return invoke(endpoint, method, null, headers, null, AppConstant.HTTP_READ_TIMEOUT);
    }

    /**
     * Invoke the endpoint with desired method and request body
     *
     * @param endpoint
     * @param method
     * @param requestBody
     * @return
     */
    public ResponseDto invoke(String endpoint, HttpMethod method, Object requestBody) {
        return invoke(endpoint, method, requestBody, null, null, AppConstant.HTTP_READ_TIMEOUT);
    }

    /**
     *
     * @param endpoint
     * @param method
     * @param requestBody
     * @param headers
     * @param queryParams
     * @return
     */
    public ResponseDto invoke(String endpoint, HttpMethod method, Object requestBody,
            MultivaluedMap<String, String> headers, MultivaluedMap<String, String> queryParams) {
        return invoke(endpoint, method, requestBody, headers, queryParams, AppConstant.HTTP_READ_TIMEOUT);
    }

    /**
     *
     * @param endpoint
     * @param method
     * @param requestBody
     * @param headers
     * @return
     */
    public ResponseDto invoke(String endpoint, HttpMethod method, Object requestBody,
            MultivaluedMap<String, String> headers) {
        return invoke(endpoint, method, requestBody, headers, null, AppConstant.HTTP_READ_TIMEOUT);
    }

    /**
     *
     * @param endpoint
     * @param method
     * @param requestBody
     * @param headers
     * @param readTimeout
     * @return
     */
    public ResponseDto invoke(String endpoint, HttpMethod method, Object requestBody,
                              MultivaluedMap<String, String> headers, int readTimeout) {
        return invoke(endpoint, method, requestBody, headers, null, readTimeout);
    }

    /**
     * Trigger the request with desired timeout
     *
     * @param endpoint
     * @param method
     * @param requestBody
     * @param headers
     * @param queryParams
     * @param readTimeout
     * @return
     */
    public ResponseDto invoke(String endpoint, HttpMethod method, Object requestBody,
            MultivaluedMap<String, String> headers, MultivaluedMap<String, String> queryParams, int readTimeout) {
        Long requestTime = Instant.now().toEpochMilli();
        endpoint = getEndpoint(endpoint, queryParams);
        Log.infof("Invoking url : %s", endpoint);
        String exceptionMessage = "";
        ResponseDto responseDto;
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder()
                    .method(method.toString(),
                            requestBody != null
                                    ? HttpRequest.BodyPublishers
                                            .ofByteArray(objectMapper.writeValueAsBytes(requestBody))
                                    : HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(readTimeout));

            headers.keySet().forEach(key -> request.header(key, String.join(",", headers.get(key))));

            HttpResponse<String> response = this.httpClient
                    .sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            Log.errorf("Exception :%s", exception);
                        } else {
                            Log.infof("Body:%s\nStatus Code:%s\nHeaders:%s", result.body(), result.statusCode(),
                                    result.headers());
                        }
                    }).toCompletableFuture().get();

            responseDto = new ResponseDto(response.statusCode(), response.body(),
                    (Instant.now().toEpochMilli() - requestTime));
            responseDto.setHeader(toMultivaluedMap(response.headers().map()));
        } catch (Exception e) {
            int statusCode = 500;
            e.printStackTrace();
            Log.errorf("Exception invoke %s", e);
            exceptionMessage = e.getMessage();
            Long responseTime = Instant.now().toEpochMilli();
            responseDto = new ResponseDto(statusCode, exceptionMessage, (responseTime - requestTime));
        }
        try {
            Long responseTime = Instant.now().toEpochMilli();
            trafficLogService.log(endpoint, requestBody != null ? objectMapper.writeValueAsString(requestBody) : null,
                    responseDto.getResponse(), requestTime, responseTime,
                    responseDto.getStatusCode(), exceptionMessage,
                    "", headers, responseDto.getHeader(), queryParams, method.toString(), true);
        } catch (Exception e) {
            Log.errorf("Exception invoke inserting to traffic logs $s", e);
        }
        Log.infof("Invoked url : %s", endpoint);
        return responseDto;
    }

    /**
     * Get Response String as UTF-8
     * 
     * @param input
     * @param headers
     * @return
     */
    // private String getUtf8(String input, HttpHeaders headers) {
    // try {
    // if (headers != null) {
    // Optional<String> mediaType = headers.firstValue("Content-Type");
    // if (mediaType.isPresent() && (mediaType.get().getCharset() ==
    // StandardCharsets.UTF_16
    // || mediaType.getCharset() == StandardCharsets.UTF_16BE
    // || mediaType.getCharset() == StandardCharsets.UTF_16LE)) {
    // input = new String(input.getBytes(mediaType.getCharset()))
    // .replaceAll("[^!-~\\u20000-\\uFE1F\\uFF00-\\uFFEF]", "");
    // }
    // }
    // } catch (Exception e) {
    // log.info("Not able to convert to utf-8");
    // }
    // return input;
    // }

    private static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static String getEndpoint(String endpoint, MultivaluedMap<String, String> queryParmas) {
        return endpoint
                + (endpoint.indexOf(AppConstant.QUESTION_MARK) == -1 ? AppConstant.QUESTION_MARK : AppConstant.EMPTY)
                + toQueryString(queryParmas);
    }

    private static String toQueryString(Map<?, ?> map) {
        if (map != null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s",
                        urlEncodeUTF8(entry.getKey().toString()),
                        urlEncodeUTF8(entry.getValue().toString())));
            }
            return sb.toString();
        }
        return AppConstant.EMPTY;
    }

    private static MultivaluedMap<String, String> toMultivaluedMap(Map<String, List<String>> input) {
        MultivaluedMap<String, String> output = new MultivaluedHashMap<String, String>();
        input.keySet().forEach(key -> output.add(key, String.join(",", input.get(key))));
        return output;
    }
}
