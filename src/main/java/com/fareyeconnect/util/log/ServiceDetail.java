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
package com.fareyeconnect.util.log;

import com.fareyeconnect.constant.AppConstant.ServiceCode;

public class ServiceDetail {

    private ServiceDetail() {
    }

    private static ThreadLocal<ServiceCode> serviceCode = new ThreadLocal<>();
    private static ThreadLocal<String> carrierCode = new ThreadLocal<>();

    public static ServiceCode getServiceCode() {
        return serviceCode.get();
    }

    public static void setServiceCode(ServiceCode serviceCode) {
        ServiceDetail.serviceCode.set(serviceCode);
    }

    public static String getCarrierCode() {
        return carrierCode.get();
    }

    public static void setCarrierCode(String carrierCode) {
        ServiceDetail.carrierCode.set(carrierCode);
    }

    public static void reset() {
        serviceCode.remove();
        carrierCode.remove();
    }

}