/**
 * ****************************************************************************
 *
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 *
 *
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
 *
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

    public enum MessageQueue {
        KAFKA("kafka"), RABBITMQ("rabbitmq");

        private final String val;

        MessageQueue(final String val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return val;
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
}