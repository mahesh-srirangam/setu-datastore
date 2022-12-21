package com.fareyeconnect.tool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class GpsUpdate {
    private String vehicleNo;
    private double latitude;
    private double longitude;
    private int speed;
    private int odometerKm;
    private int mainPower;
    private int signalStrength;
    private int batteryPower;
    private JsonNode extraInfo;
    private String istTimestamp;
    private String vendorCode;
    private String transporterCode;
    private String orgId;

    public static void validate(GpsUpdate gpsUpdate) {
        String missingProperty = null;
        if (gpsUpdate.getVehicleNo() == null) {
            missingProperty = "vehicle_no";
        } else if (gpsUpdate.getLatitude() == 0L) {
            missingProperty = "latitude";
        } else if (gpsUpdate.getLongitude() == 0L) {
            missingProperty = "longitude";
        } else if (gpsUpdate.getIstTimestamp() == null) {
            missingProperty = "ist_timestamp";
        } else if (gpsUpdate.getVendorCode() == null) {
            missingProperty = "vendor_code";
        } else if (gpsUpdate.getTransporterCode() == null) {
            missingProperty = "transporter_code";
        } else if (gpsUpdate.getOrgId() == null) {
            missingProperty = "org_id";
        }
        if (missingProperty != null) {
            throw new IllegalArgumentException("Missing required property : " + missingProperty);
        }
    }
}
