package com.fareyeconnect.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceKey {

    private String connector;
    private String service;
    private String organizationId;
}
