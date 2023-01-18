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
import io.quarkus.panache.common.Parameters;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

/**
 * @author Baldeep Singh Kwatra
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Connector extends AbstractEntity {

    @NotBlank
    private String code;

    @NotNull
    private String name;

    private String notes;

    private int version;

    private String status;

    private boolean shared;

    public static Uni<Connector> findByCodeAndVersionAndCreatedByOrg(String code, int version, String organizationId) {
        return find("code= :code and version= :version and createdByOrg= :createdByOrg",
                Parameters.with("code", code).and("version", version).and("createdByOrg", organizationId)).firstResult();
    }
}