package com.fareyeconnect.tool.service;

import com.fareyeconnect.exception.AppException;
import com.fareyeconnect.tool.model.Service;
import com.fareyeconnect.tool.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;


@ApplicationScoped
public class FlowExecutionService {

    @Inject
    ServiceService serviceService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    BeanManager beanManager;

    public void initiateFlowExecution(String connectorCode, String serviceCode, String request) throws JsonProcessingException, ExecutionException, InterruptedException {
        Uni<Service> serviceUni = serviceService.findByServiceCode(serviceCode);
        String language = "js";
        try (Context context = buildContext(language, request)) {
            executeFlow(context, serviceUni);
        } catch (Exception e) {
            throw new AppException("Flow execution failed with error " + e.getMessage());
        }
    }

    private Context buildContext(String language, String request) {
        Context context = Context.newBuilder()
                .allowAllAccess(true)
                .allowCreateThread(true)
                .allowCreateProcess(true)
                .build();
        Value languageBindings = context.getBindings(language);

        languageBindings.putMember("request", request);
        languageBindings.putMember("PROPERTY", "");
        languageBindings.putMember("VARIABLE", "");
        return context;
    }

    private void executeFlow(Context context, Uni<Service> serviceUni) throws ExecutionException, InterruptedException, ClassNotFoundException, JsonProcessingException {
        String startTaskNumber = "1";
        Service service = objectMapper.readValue("{\"allowAudit\":true,\"code\":\"tracking-pull\",\"name\":\"Tracking Pull\",\"version\":0,\"flow\":[{\"type\":\"Start\",\"taskNumber\":\"1\",\"nextTask\":[\"2\"]},{\"type\":\"SystemTask\",\"taskNumber\":\"2\",\"nextTask\":[\"3\"],\"task\":\"console.log('Hello World(1)')\"},{\"type\":\"SystemTask\",\"taskNumber\":\"3\",\"nextTask\":[\"4\"],\"task\":\"console.log('Hello World (2)')\"},{\"taskNumber\":\"4\",\"type\":\"SystemTask\",\"task\":\"console.log('Hello World (4)')\",\"nextTask\":[]}]}", Service.class);//serviceUni.subscribe().asCompletionStage().get();
        if (service == null)
            throw new AppException("Service doesn't exist");
        Map<String, Task> flowMap = service.getFlow().stream().collect(Collectors.toMap(Task::getTaskNumber, Function.identity()));
        executeFlow(startTaskNumber, flowMap, context);
    }

    public void executeFlow(String startTask, Map<String, Task> flowMap, Context context) throws ExecutionException, InterruptedException {
        Queue<Task> taskQueue = new LinkedList<>();
        taskQueue.add(flowMap.get(startTask));
        while (!taskQueue.isEmpty()) {
            if (taskQueue.size() > 1) {
                List<Task> taskList = new ArrayList<>(taskQueue);
                List<String> nextTaskList = new ArrayList<>();
                taskQueue.clear();
                List<CompletableFuture<Void>> taskWorkers = new ArrayList<>();
                for (Task task : taskList) {
                    CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
                        System.out.println("Executing task {} millis {}" + task.getTaskNumber() + System.currentTimeMillis());
                        Context context1 = buildContext("js","");
                        task.execute(context1);
                        nextTaskList.addAll(task.getNextTask());
                        return null;
                    });
                    taskWorkers.add(completableFuture);
                }
                CompletableFuture.allOf(taskWorkers.toArray(new CompletableFuture[taskList.size()])).get();
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
