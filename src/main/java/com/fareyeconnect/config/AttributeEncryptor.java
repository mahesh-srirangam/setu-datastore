

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
package com.fareyeconnect.config;

import javax.inject.Singleton;
import javax.persistence.AttributeConverter;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fareyeconnect.util.CryptoUtil;

import io.quarkus.arc.Unremovable;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 16-Feb-2022, 7:46:22 PM
 */
@Singleton
@Unremovable
public class AttributeEncryptor implements AttributeConverter<String, String> {


    @ConfigProperty(name = "encrypt.key")
    private String encryptKey;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return CryptoUtil.encrypt(attribute, encryptKey);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return CryptoUtil.decrypt(dbData, encryptKey);
    }
}