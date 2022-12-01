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

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.fareyeconnect.config.security.GatewayUser;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

/**
 * This entity is abstract base entity for all entities. It contains common
 * attributes required in each of the entity
 *
 * @author Baldeep Singh Kwatra
 */
@Data
@MappedSuperclass
public abstract class AbstractEntity extends PanacheEntityBase implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String createdBy;

    private String modifiedBy;

    private String createdByOrg;

    private String modifiedByOrg;

    @Transient
    private boolean allowAudit = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modified;

    @PrePersist
    public void prePersist() {
        if (this.allowAudit) {
            GatewayUser gatewayUser = GatewayUser.getUser();
            this.createdBy = gatewayUser.getId();
            this.createdByOrg = gatewayUser.getOrganizationId();
            this.modifiedBy = gatewayUser.getId();
            this.modifiedByOrg = gatewayUser.getOrganizationId();
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.allowAudit) {
            GatewayUser gatewayUser = GatewayUser.getUser();
            this.modifiedBy = gatewayUser.getId();
            this.modifiedByOrg = gatewayUser.getOrganizationId();
        }
    }
}