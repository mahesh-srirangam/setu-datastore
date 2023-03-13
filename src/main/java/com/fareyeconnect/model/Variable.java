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
package com.fareyeconnect.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fareyeconnect.config.AttributeEncryptor;
import com.fareyeconnect.tool.model.Connector;
import com.fareyeconnect.util.BeanUtil;

import io.quarkus.panache.common.Parameters;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 *
 * @author Baldeep Singh Kwatra
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "`variable`")
public class Variable extends AbstractEntity {

    @NotBlank
    String key;

    @NotNull
    String value;

    public String notes;

    public void setValue(String value) {
        this.value = BeanUtil.bean(AttributeEncryptor.class).convertToDatabaseColumn(value);

    }

    public String getValue() {
        return BeanUtil.bean(AttributeEncryptor.class).convertToEntityAttribute(value);
    }

}