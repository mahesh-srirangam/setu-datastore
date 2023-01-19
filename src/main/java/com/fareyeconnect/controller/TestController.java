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
package com.fareyeconnect.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.fareyeconnect.tool.Node;
import com.fareyeconnect.util.email.Email;
import com.fareyeconnect.util.email.EmailService;
import com.fareyeconnect.util.log.TrafficLogService;
import com.fareyeconnect.util.rest.RequestDto;
import com.fareyeconnect.util.rest.RestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.handler.codec.http.HttpMethod;

import org.graalvm.polyglot.HostAccess;

@Path("/hello")
public class TestController {

    @Inject
    RestService restService;

    @Inject
    EmailService emailService;

    @Inject
    ObjectMapper objectMapper;

    // @Inject
    // Property property;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "HELLO";// + property.getKafkaBootstrapServers();
    }

    @POST
    @Path("rest")
    public Object rest(RequestDto requestDto) {
        return restService.invoke(requestDto.getUrl(), HttpMethod.valueOf(requestDto.getMethod()), requestDto.getBody(),
                requestDto.getHeader(), requestDto.getQueryParam(), requestDto.getReadTimeout());
    }

    @POST
    @Path("email")
    public void email(Email email) {
        emailService.sendEmail(email);
    }

    @Inject
    TrafficLogService trafficLogService;

    static String JS_CODE = "(function fibonacci(num) { var a = 1, b = 0, temp; while (num >= 0){ temp = a; a = a + b; b = temp; num--; } return b; })";
    static String FLOW_CODE = "[ { \"id\": \"1\", \"parentId\": -1, \"nodeComponent\": \"Node\", \"data\": { \"text\": \"Start\", \"title\": \"Start\", \"description\": \"\" } }, { \"id\": 3711, \"parentId\": \"1\", \"nodeComponent\": \"Node\", \"data\": { \"description\": \"Perform file based operations\", \"title\": \"File Operation\" }, \"code\": \"/**Write your code here**/\\nconsole.log(\\\"Perform File Based Operation\\\");\\nvar System = Java.type(\\\"java.lang.System\\\");\\nSystem.out.println(\\\"System out println being done.\\\")\" }, { \"id\": 1866, \"parentId\": 5244, \"nodeComponent\": \"Node\", \"data\": { \"description\": \"Perform file based operations\", \"title\": \"File Operation\" }, \"code\": \"/**Write your code here**/\\nconsole.log(\\\"Perform File Based Operation 2\\\");\" }, { \"id\": 5244, \"parentId\": \"1\", \"nodeComponent\": \"Node\", \"data\": { \"description\": \"Write your custom <b>logic</b> here</span>\", \"title\": \"System Task\" }, \"code\": \"console.log(\\\"System Task 1\\\")\" } ]";

    Context context = Context.newBuilder("js")
            .allowHostAccess(HostAccess.ALL)
            // allows access to all Java classes
            .allowHostClassLookup(className -> true)
            .build();

    private void executeCode(List<Node> nodeList) {
        for (Node item : nodeList) {
            if (item.getCode() != null) {
                Value result = context.eval("js", item.getCode());
                // System.out.println("--" + result.asString());
            }
            if (!item.getChildren().isEmpty()) {
                executeCode(item.getChildren());
            }
        }
    }

    public void code() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<Node> flow = objectMapper.readValue(FLOW_CODE,
                    new TypeReference<List<Node>>() {
                    });
            executeCode(Node.toTree(flow));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Context context = Context.create();
        // Value result = context.eval("js", "40+2");
        // Value result2 = context.eval("js", JS_CODE);
        // System.out.println(result2.execute(1_0));
        // System.out.println(result.asInt() == 42);
        // test1();
        new TestController().code();
    }

    public static void test1() {
        Context context = Context.newBuilder("js").allowHostAccess(HostAccess.ALL).build();
        context.getBindings("js").putMember("d", new Data());
        context.eval("js", "var x = d.name;");
        System.out.println(context.getBindings("js").getMember("x").asString());
    }

    public static class Data {
        public String name = "JKK";

        public String getName() {
            return this.name;
        }
    }

}