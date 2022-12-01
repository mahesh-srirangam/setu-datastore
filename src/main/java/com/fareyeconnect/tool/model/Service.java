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
package com.fareyeconnect.tool.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fareyeconnect.model.AbstractEntity;
import com.fareyeconnect.tool.Node;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import io.smallrye.common.constraint.NotNull;
import lombok.Data;

/**
 *
 * @author Baldeep Singh Kwatra
 */
@Data
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Service extends AbstractEntity {

    @NotBlank
    String code;

    @NotNull
    String name;

    public String notes;

    public boolean published;

    public int version;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    List<Node> node;

    @OneToOne 
    @JsonIgnore
    public Connector connector;

}