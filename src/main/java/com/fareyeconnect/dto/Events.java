package com.fareyeconnect.dto;

import lombok.Data;

@Data
public class Events {

    private String headers;
    private Object body;
    private String url = "";
    private String sender;
    private String orgId;
    private String topic;
    private String bootstrapServerAddress;
}
