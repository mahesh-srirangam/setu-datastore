/**
 * ****************************************************************************
 *
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 *
 *
 * NOTICE: All information contained herein is, and remains the property of
 * FaEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye
 */
package com.fareyeconnect.util;

import java.time.Duration;
import com.fareyeconnect.constant.AppConstant;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 18-May-2022, 9:55:03 AM
 */
public class AppUtil {

    private AppUtil() {
        throw new IllegalStateException("AppUtil is a utility class");
    }

    // public static RestTemplate getRestTemplate(int readTimeout) {
    //     return getRestTemplate(readTimeout, AppConstant.HTTP_CONNECT_TIMEOUT);
    // }

    // public static RestTemplate getRestTemplate(int readTimeout, int connectTimeout) {
    //     return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(connectTimeout))
    //             .setReadTimeout(Duration.ofSeconds(readTimeout)).build();
    // }

    public static String camelToSnake(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}