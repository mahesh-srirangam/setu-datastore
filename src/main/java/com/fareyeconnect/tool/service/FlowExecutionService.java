package com.fareyeconnect.tool.service;

import com.fareyeconnect.config.Property;
import com.fareyeconnect.constant.AppConstant.ContextMember;
import com.fareyeconnect.constant.AppConstant.Language;
import com.fareyeconnect.exception.AppException;
import com.fareyeconnect.service.VariableService;
import com.fareyeconnect.tool.helpers.KafkaHelper;
import com.fareyeconnect.tool.helpers.UtilHelper;
import com.fareyeconnect.tool.model.Service;
import com.fareyeconnect.tool.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;


@ApplicationScoped
public class FlowExecutionService {


    @Inject
    ConnectorService connectorService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    VariableService variableService;

    @Inject
    Property property;


    public void initiateFlowExecution(String connectorCode, String serviceCode, String request) {
        Service service = connectorService.getService(serviceCode, connectorCode);
        if (service == null)
            throw new AppException("Service code doesn't exist");

        Language language = Language.JS;
        try (Context context = buildContext(language, request, null)) {
            executeFlow(context, service);
        } catch (Exception e) {
            throw new AppException("Flow execution failed with error " + e.getMessage());
        }
    }

    private Context buildContext(Language language, String request, String currentTask) {
        HostAccess hostAccess = HostAccess.newBuilder()
                .allowAccessAnnotatedBy(HostAccess.Export.class)
                .build();
        Context context = Context.newBuilder()
                .allowHostAccess(hostAccess)
                .build();
        Value bindings = context.getBindings(language.toString());
        bindings.putMember(ContextMember.REQUEST, request);
        bindings.putMember(ContextMember.RESPONSE, null);
        bindings.putMember(ContextMember.VARIABLE, null);
        bindings.putMember(ContextMember.RESPONSE, null);
        bindings.putMember(ContextMember.PROPERTY, property);
        if (currentTask != null) {
            bindings.putMember("temp_" + currentTask, null);
            context.eval(language.toString(), "var temp_" + currentTask + "={}");
        }
        registerHelperMethods(bindings);
        return context;
    }

    private void registerHelperMethods(Value value) {
        value.putMember(ContextMember.FUNC_UTIL, new UtilHelper());
        value.putMember(ContextMember.FUNC_KAFKA, new KafkaHelper());
    }

    private void executeFlow(Context context, Service service) throws ExecutionException, InterruptedException, ClassNotFoundException, JsonProcessingException {
        String startTaskNumber = "1";
        Map<String, Task> flowMap = service.getFlow().stream().collect(Collectors.toMap(Task::getTaskNumber, Function.identity()));
        executeFlow(startTaskNumber, flowMap, context);
    }

    public void executeFlow(String startTask, Map<String, Task> flowMap, Context context) throws ExecutionException, InterruptedException {
        Queue<Task> taskQueue = new LinkedList<>();
        taskQueue.add(flowMap.get(startTask));
        Value mainContextBindings = context.getBindings(Language.JS.toString());
        while (!taskQueue.isEmpty()) {
            if (taskQueue.size() > 1) {
                List<Task> taskList = new ArrayList<>(taskQueue);
                List<String> nextTaskList = new ArrayList<>();
                taskQueue.clear();
                List<CompletableFuture<Void>> taskWorkers = new ArrayList<>();
                Map<String, Value> taskContextBindings = new HashMap<>();
                for (Task task : taskList) {
                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                        Context taskContext = buildContext(Language.JS, "", task.getTaskNumber());
                        String contextMember = "temp_" + task.getTaskNumber();
                        task.execute(taskContext);
                        taskContextBindings.put(contextMember, taskContext.getBindings(Language.JS.toString()).getMember(contextMember));
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
                System.out.println("Executing task {}" + task.getTaskNumber());
                if (task.getNextTask() != null)
                    task.getNextTask().forEach(nextTask -> {
                        if (!taskQueue.contains(flowMap.get(nextTask)))
                            taskQueue.add(flowMap.get(nextTask));

                    });
            }
        }
    }
}
