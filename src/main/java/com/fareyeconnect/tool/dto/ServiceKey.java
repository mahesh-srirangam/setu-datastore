package com.fareyeconnect.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ServiceKey {

    private String connector;
    private String service;
    private String organizationId;
    private int connectorVersion;
}
