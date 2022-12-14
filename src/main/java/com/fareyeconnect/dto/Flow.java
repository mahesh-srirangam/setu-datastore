package com.fareyeconnect.dto;

import com.fareyeconnect.tool.task.Task;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Flow {


    private List<Task> task;

}
