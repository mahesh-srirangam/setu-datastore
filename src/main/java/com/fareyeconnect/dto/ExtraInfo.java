package com.fareyeconnect.dto;

import lombok.Data;

@Data
public class ExtraInfo {

    private String comments;
    private String promisedDeliveryDate;
    private String expectedDeliveryDate;
    private String receivedBy;
    private String relation;
    private String epod;
    private String signature;
    private String customerCode;
    private String customerName;
}
