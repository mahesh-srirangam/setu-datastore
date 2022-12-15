/**
 * ****************************************************************************
 * <p>
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 * <p>
 * <p>
 * NOTICE: All information contained herein is, and remains the property of
 * FarEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye.
 */
package com.fareyeconnect.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Baldeep Singh Kwatra
 * @since 03-Dec-2022, 9:55:03 AM
 */
public final class AppConstant {

    public static final String DEFAULT_ID = "1";
    public static final int HTTP_CONNECT_TIMEOUT = 5;
    public static final int HTTP_READ_TIMEOUT = 30;
    public static final String NOT_SUPPORTED = "Not supported yet.";
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String QUESTION_MARK = "?";

    public static final String UNDERSCORE="_";

    public static final String PARSER_PACKAGE="com.fareyeconnect.tool.parser.";
    public static final String PARSER="Parser";

    public enum Language {
        JS("js"), PYTHON("python"), RUBY("ruby"), R("R"),LLVM("llvm");

        private final String language;

        Language(final String text) {
            this.language = text;
        }

        @Override
        public String toString() {
            return language;
        }
    }

    public enum ServiceCode {
        GENERATE_AWB("generate-awb"), CANCEL_AWB("cancel-awb"), TRACKING_PULL("tracking-pull"),
        RESCHEDULE("reschedule"), GET_SLOT("get-slot"), RESERVE_SLOT("reserve-slot"), GPS_UPDATES("gps-updates"),
        TRACKING_SUBSCRIBE("tracking-subscribe"), WEBHOOK("webhook");

        private final String text;

        ServiceCode(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum TypeOfOrder {
        FORWARD("Forward"), REVERSE("Reverse"), DROPOFF("Dropoff"), FTL("FTL"), OCEAN("Ocean"), EXCHANGE("Exchange");

        private final String text;

        TypeOfOrder(final String text) {
            this.text = text;
        }

        // Reverse-lookup map for getting a day from an abbreviation
        private static final Map<String, TypeOfOrder> lookup = new HashMap<>();

        static {
            for (TypeOfOrder typeOfOrder : TypeOfOrder.values()) {
                lookup.put(typeOfOrder.toString(), typeOfOrder);
            }
        }

        public static TypeOfOrder get(String val) {
            return lookup.get(val);
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum Flow {
        START, REST, SCRIPT, FILE
    }

    public static class ContextMember {
        public static final String REQUEST = "request";
        private static final String REQUEST_DOC = "The request sent by user on setu service invocation";
        public static final String RESPONSE = "response";
        private static final String RESPONSE_DOC = "The final response that is to be sent to the setu service caller";
        public static final String TEMP = "temp";
        private static final String TEMP_DOC = "The variable in which temp data can be stored and can be used between flow tasks";
        public static final String FUNC_UTIL = "fu";
        private static final String FUNC_UTIL_DOC = "All utility functions can be accessed with fu.functionName() eg fu.getDate(); in system task";
        public static final String FUNC_KAFKA = "fk";
        private static final String FUNC_KAFKA_DOC = "All kafka helper function can be accessed with fk.functionname() eg fk.pushStatusUpdate(); in system task";
        public static final String VARIABLE = "variable";
        private static final String VARIABLE_DOC = "All variables can be accessed with VARIABLE.key_name eg VARIABLE.name";
        public static final String PROPERTY = "prop";
        private static final String PROPERTY_DOC = "All properties on config server can be accessed with PROPERTY.get['key']";

    }


}