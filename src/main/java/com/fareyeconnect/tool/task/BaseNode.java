package com.fareyeconnect.tool.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseNode {

    private String nodeName;
    private String nodeId;
    private String outputState;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long executionTime;
    private String nextNode;

}
