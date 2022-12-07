package com.fareyeconnect.service;

import com.fareyeconnect.config.Property;
import com.fareyeconnect.config.security.GatewayUser;
import com.fareyeconnect.model.Variable;
import org.json.JSONObject;
import org.json.XML;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class UtilHelper {

    @Inject
    private static Property property;

    @Inject
    private static VariableService variableService;
    public static String env() {
        return property.getEnvironment();
    }

    public static String uuid() {
        return String.valueOf(UUID.randomUUID());
    }

    public static String getUserId() {
        return GatewayUser.getUser().getId();
    }

    public static GatewayUser getUser() {
        return GatewayUser.getUser();
    }

    public static String getUsername() {
        return GatewayUser.getUser().getEmail();
    }

    public static String getOrgId() {
        return GatewayUser.getUser().getOrganizationId();
    }

    public static String getDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getDateTime(String format, String timeZone) {
        return LocalDateTime.now(ZoneId.of(timeZone)).format(DateTimeFormatter.ofPattern(format));
    }

    public static String convertDate(String date, String format) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern(format));
    }

    public static String convertDate(String date, String fromFormat, String fromZone, String toFormat, String toZone) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(fromFormat).withZone(ZoneId.of(fromZone))).format(DateTimeFormatter.ofPattern(toFormat).withZone(ZoneId.of(toZone)));
    }

    public String xmlToJson(String xml) {
        return XML.toJSONObject(xml).toString();
    }

    public static String jsonToXml(String json) {
        return XML.toString(new JSONObject(json));
    }

    public static void saveVariableInOrg(String name, String value, String orgId) {
        Variable variable = new Variable();
        variable.setKey(name);
        variable.setValue(value);
        variable.setCreatedByOrg(orgId);
        variableService.save(variable);
    }

    public static void saveVariable(String name, String value) {
        saveVariableInOrg(name, value, null);
    }

//    public static String safeString(String unsafeString) {
//
//    }

    public static void invokeService(String serviceCode, String requestBodyAsString){

    }
}
