package com.fareyeconnect.tool.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.graalvm.polyglot.Context;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RestCall.class, name = "Rest"),
        @JsonSubTypes.Type(value = Start.class, name = "Start"),
        @JsonSubTypes.Type(value = SystemTask.class, name = "SystemTask"),
        @JsonSubTypes.Type(value = FileService.class, name = "FileServices"),
})
@Data
public abstract class Task {

    private String taskNumber;

    private String type;
    private List<String> nextTask;


    public abstract void execute(Context context);
}
