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
package com.fareyeconnect.tool.model;

import com.fareyeconnect.model.AbstractEntity;
import com.fareyeconnect.tool.dto.Config;
import com.fareyeconnect.tool.task.Task;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.quarkus.panache.common.Parameters;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Baldeep Singh Kwatra
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Service extends AbstractEntity {

    @NotBlank
    private String code;

    @NotNull
    private String name;

    private String notes;

    private int version;

    private String category;

    private String status;

    private String trigger;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<Task> flow;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Config config;

    @ManyToOne
    @NotNull
    private Connector connector;

    public static Uni<Service> findByCode(String serviceCode) {
        return find("code", serviceCode).firstResult();
    }

    public static Uni<Service> findById(String id) {
        return find("id", id).firstResult();
    }

    public static Uni<List<Service>> findByActive(String status) {
        return list("status", status);
    }

    public static Uni<Service> findByConnectorServiceCodeStatus(Connector connector, String serviceCode, String status) {
        return find("connector= :connector and code= :code and status= :status", Parameters.with("connector", connector).
                and("code", serviceCode).and("status", status)).firstResult();
    }
}