package com.fareyeconnect.service;

import com.fareyeconnect.config.Property;
import com.fareyeconnect.config.security.GatewayUser;
import com.fareyeconnect.model.Variable;
import com.fareyeconnect.tool.Helper;
import org.eclipse.jgit.util.Hex;
import org.json.JSONObject;
import org.json.XML;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@ApplicationScoped
public class UtilHelper {

    @Inject
    private static Property property;

    private static final String HMAC_SHA_256 = "HmacSHA256";

    @Inject
    private static VariableService variableService;
//    public static String env() {
//        return property.getEnvironment();
//    }

    @Helper(description = "Returns random uuid")
    public static String uuid() {
        return String.valueOf(UUID.randomUUID());
    }

    @Helper(description = "Current User")
    public static String userId() {
        return GatewayUser.getUser().getId();
    }

    public static GatewayUser user() {
        return GatewayUser.getUser();
    }

    public static String username() {
        return GatewayUser.getUser().getEmail();
    }

    public static String org() {
        return GatewayUser.getUser().getOrganizationId();
    }

    public String date() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String dateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String dateTime(String format, String timeZone) {
        return LocalDateTime.now(ZoneId.of(timeZone)).format(DateTimeFormatter.ofPattern(format));
    }

    public static String date(String date, String format) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern(format));
    }

    public static String date(String date, String fromFormat, String fromZone, String toFormat, String toZone) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(fromFormat).withZone(ZoneId.of(fromZone))).format(DateTimeFormatter.ofPattern(toFormat).withZone(ZoneId.of(toZone)));
    }

    public static String json(String xml) {
        return XML.toJSONObject(xml).toString();
    }

    public static String xml(String json) {
        return XML.toString(new JSONObject(json));
    }

    public static void saveVariable(String name, String value, String orgId) {
        Variable variable = new Variable();
        variable.setKey(name);
        variable.setValue(value);
        variable.setCreatedByOrg(orgId);
        variableService.save(variable);
    }

    public static void saveVariable(String name, String value) {
        saveVariable(name, value, null);
    }

//    public static String safeString(String unsafeString) {
//
//    }

    public static String base64Encode(String text){
        return Base64.getEncoder().encodeToString(text.getBytes());
    };

    public static String base64Decode(String text){
        return new String(Base64.getDecoder().decode(text.getBytes()));
    };

    public static String encodeHMACSha256Base64(String key, String data){
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance(HMAC_SHA_256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA_256);
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return Base64.getEncoder().encodeToString(hmacSha256);
    };

    public static String encodeHMACSha256Hex(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256HMAC = Mac.getInstance(HMAC_SHA_256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);
        sha256HMAC.init(secretKeySpec);
        return Hex.toHexString(sha256HMAC.doFinal(data.getBytes()));
    }

    public static String encodeSHA256Base64(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }
    public static void invokeService(String serviceCode, String requestBodyAsString){

    }
}
