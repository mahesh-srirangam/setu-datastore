package com.fareyeconnect.tool.dto;

import lombok.Data;

@Data
public class Config {

    private String requestSchema;

    private String responseSchema;

    private String requestContentType;
}
