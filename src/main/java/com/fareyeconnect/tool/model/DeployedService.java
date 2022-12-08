package com.fareyeconnect.tool.model;

import com.fareyeconnect.tool.task.TaskList;
import lombok.Data;

@Data
public class DeployedService {
    private String name;
    private String code;
    private String connectorCode;

    private TaskList node;

}
