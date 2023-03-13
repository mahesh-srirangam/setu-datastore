/*
 *
 *  * *
 *  *  * ****************************************************************************
 *  *  *
 *  *  * Copyright (c) 2023, FarEye and/or its affiliates. All rights
 *  *  * reserved.
 *  *  * ___________________________________________________________________________________
 *  *  *
 *  *  *
 *  *  * NOTICE: All information contained herein is, and remains the property of
 *  *  * FaEye and its suppliers,if any. The intellectual and technical concepts
 *  *  * contained herein are proprietary to FarEye. and its suppliers and
 *  *  * may be covered by us and Foreign Patents, patents in process, and are
 *  *  * protected by trade secret or copyright law. Dissemination of this information
 *  *  * or reproduction of this material is strictly forbidden unless prior written
 *  *  * permission is obtained from FarEye
 *  *
 *
 */

package com.fareyeconnect.tool.service;


import com.fareyeconnect.config.Property;
import com.fareyeconnect.exception.AppException;
import com.fareyeconnect.service.VariableService;
import com.fareyeconnect.tool.dto.Config;
import com.fareyeconnect.tool.helpers.KafkaHelper;
import com.fareyeconnect.tool.helpers.UtilHelper;
import com.fareyeconnect.tool.model.Service;
import com.fareyeconnect.tool.parser.Parser;
import com.fareyeconnect.tool.task.Task;
import com.fareyeconnect.util.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.xml.bind.JAXBException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fareyeconnect.constant.AppConstant.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Hemanth Reddy
 * @since 16/01/23
 */
@ApplicationScoped
public class FlowExecutionService {


    @Inject
    ConnectorService connectorService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    VariableService variableService;

//    @Inject
//    PropertyService property;

    @Inject
    Property property;

    @Inject
    ServicePublisherService servicePublisherService;


    /**
     * Below service fetches the service and execute the service
     * <p>
     * Validate request
     * Build graal engine context
     * Execute flow
     * Parse response
     *
     * @param connectorCode
     * @param serviceCode
     * @param requestBody
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     * @throws JsonProcessingException
     * @throws JAXBException
     */
    public Uni<RestResponse<String>> initiateFlowExecution(String connectorCode, int connectorVersion, String serviceCode, String requestBody) throws XMLStreamException, ClassNotFoundException, JsonProcessingException, JAXBException, ExecutionException, InterruptedException {
        Uni<Service> serviceUni = servicePublisherService.fetchLiveVersion(connectorCode, connectorVersion, serviceCode);
        return serviceUni.onItem().transformToUni(service -> {
            if (service == null)
                throw new AppException("Service you are trying to excute doesn't exist", RestResponse.Status.NOT_FOUND.getStatusCode());
            else {
                try {
                    Object request = validateAndBuildRequest(service, requestBody);
                    Language language = Language.valueOf(property.getScriptEngineLanguage());
                    try (Context context = buildContext(language, request, null)) {
                        executeFlow(context, service, language);
                        RestResponse<String> response = extractResponse(context, language);
                        Log.info("Response " + response);
                        return Uni.createFrom().item(response);
                    } catch (Exception e) {
                        throw new AppException("Flow execution failed with error " + e.getMessage());
                    }
                } catch (Exception e) {
                    throw new AppException(e.getMessage());
                }
            }
        });
    }

    /**
     * Build graal engine based on the Language provided
     *
     * @param language
     * @param request
     * @param tempTaskVar
     * @return
     */
    private Context buildContext(Language language, Object request, String tempTaskVar) {
        HostAccess hostAccess = HostAccess.newBuilder()
                .allowAccessAnnotatedBy(HostAccess.Export.class)
                .build();
        Context context = Context.newBuilder()
                .allowHostAccess(hostAccess)
                .build();
        Value bindings = context.getBindings(language.toString());
        bindings.putMember(ContextMember.REQUEST, request);
        bindings.putMember(ContextMember.RESPONSE, null);
        bindings.putMember(ContextMember.VARIABLE, variableService.getVariables());
        bindings.putMember(ContextMember.STATUS, null);
        bindings.putMember(ContextMember.PROPERTY, null);
        if (tempTaskVar != null) {
            bindings.putMember(tempTaskVar, null);
            context.eval(language.toString(), "var " + tempTaskVar + "={}");
        }
        bindings.putMember(ContextMember.FUNC_UTIL, new UtilHelper());
        bindings.putMember(ContextMember.FUNC_KAFKA, new KafkaHelper());
        return context;
    }

    /**
     * Extract final response from the engine after service execution
     *
     * @param context
     * @param language
     * @return
     * @throws JsonProcessingException
     */
    private RestResponse<String> extractResponse(Context context, Language language) throws JsonProcessingException {
        Value contextBindings = context.getBindings(language.toString());
        Value response = contextBindings.getMember(ContextMember.RESPONSE);
        Value statusCode = contextBindings.getMember(ContextMember.STATUS);
        Map responseNode = response.as(Map.class);
        return RestResponse.status(RestResponse.Status.fromStatusCode(statusCode.asInt()), objectMapper.writeValueAsString(responseNode));
    }

    /**
     * Validate and parse the upcoming request based on config details
     *
     * @param service
     * @param requestBody
     * @return
     * @throws ClassNotFoundException
     * @throws XMLStreamException
     * @throws JsonProcessingException
     * @throws JAXBException
     */
    private Object validateAndBuildRequest(Service service, String requestBody) throws ClassNotFoundException, XMLStreamException, JsonProcessingException, JAXBException {
        Config config = service.getConfig();
        String requestContentType;
        if (config == null) {
            throw new AppException("Schema config details not found");
        }
        requestContentType = config.getReqContentType();
        if (requestContentType == null || requestContentType.isEmpty())
            requestContentType = APPLICATION_JSON;
        Class<?> clazz = Class.forName(PARSER_PACKAGE + requestContentType + PARSER);
        Parser parser = (Parser) BeanUtil.bean(clazz);
        return parser.parse(config.getReqSchema(), requestBody);
    }

    /**
     * Once the context and service are available, start the flow execution with start task
     *
     * @param context
     * @param service
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws JsonProcessingException
     */
    private void executeFlow(Context context, Service service, Language language) throws ExecutionException, InterruptedException {
        String startTaskNumber = "1";
        Map<String, Task> flowMap = service.getFlow().stream().collect(Collectors.toMap(Task::getTaskNumber, Function.identity()));
        executeFlow(startTaskNumber, flowMap, context, language);
    }

    /**
     * Start the flow execution
     * In case of parallel tasks schedule the task with CF
     * Merge the results and put the result into script engine
     *
     * @param startTask
     * @param flowMap
     * @param context
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void executeFlow(String startTask, Map<String, Task> flowMap, Context context, Language language) throws ExecutionException, InterruptedException {
        Queue<Task> taskQueue = new LinkedList<>();
        taskQueue.add(flowMap.get(startTask));
        Value mainContextBindings = context.getBindings(language.toString());
        while (!taskQueue.isEmpty()) {
            if (taskQueue.size() > 1) {
                List<Task> taskList = new ArrayList<>(taskQueue);
                List<String> nextTaskList = new ArrayList<>();
                taskQueue.clear();
                List<CompletableFuture<Void>> taskWorkers = new ArrayList<>();
                Map<String, Value> taskContextBindings = new HashMap<>();
                for (Task task : taskList) {
                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                        String contextMember = ContextMember.TEMP + UNDERSCORE + task.getTaskNumber();
                        Context taskContext = buildContext(language, "", contextMember);
                        task.execute(taskContext);
                        taskContextBindings.put(contextMember, taskContext.getBindings(language.toString()).getMember(contextMember));
                        nextTaskList.addAll(task.getNextTask());
                    });
                    taskWorkers.add(completableFuture);
                }
                CompletableFuture.allOf(taskWorkers.toArray(new CompletableFuture[taskList.size()])).get();
                taskContextBindings.forEach(mainContextBindings::putMember);
                nextTaskList.forEach(nextTask -> {
                    if (!taskQueue.contains(flowMap.get(nextTask)))
                        taskQueue.add(flowMap.get(nextTask));
                });
            } else {
                Task task = taskQueue.poll();
                task.execute(context);
                if (task.getNextTask() != null)
                    task.getNextTask().forEach(nextTask -> {
                        if (!taskQueue.contains(flowMap.get(nextTask)))
                            taskQueue.add(flowMap.get(nextTask));

                    });
            }
        }
    }
}
