package com.fareyeconnect.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.reinert.jjschema.SchemaIgnore;
import io.smallrye.common.constraint.Nullable;
import lombok.Data;

@Data
public class StatusUpdateRequestDto {

    private String orderNo;
    private String type;
    private String value;
    private String fareyeStatus;
    private String fareyeStatusCode;
    private String fareyeStatusDescription;
    private String fareyeSubStatus;
    private String fareyeSubStatusCode;
    private String fareyeSubStatusDescription;
    private String carrierCode;
    private String carrierStatus;
    private String carrierStatusCode;
    private String carrierStatusDescription;
    private String carrierSubStatus;
    private String carrierSubStatusCode;
    private String carrierSubStatusDescription;
    private String statusReceivedAt;
    private String locationCode;
    private String locationName;
    private String destinationLocation;
    private Float latitude;
    private Float longitude;
    private String previousStatus;
    private String previousStatusTime;
    private String lastUpdatedAt;
    @Nullable
    private String statusIdentifierValue;
    @Nullable
    private String shipmentMode;

    private ExtraInfo extraInfo;

    @SchemaIgnore
    private String callbackUrl; // For sending FarEye url to GTS

    @SchemaIgnore
    private Long id; // For internal use


    private String orgId;

    private String carrierStatusTimeGmt;

    @Nullable
    private JsonNode info;

    @Nullable
    private String carrierEta;

    @Nullable
    private String fareyeEta;

    @Nullable
    private String lastExecutedOn;

    @Nullable
    private String nextExecutionAt;
}
