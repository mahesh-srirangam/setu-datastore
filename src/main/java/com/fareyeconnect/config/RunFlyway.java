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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.logging.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;

import io.quarkus.runtime.StartupEvent;

/**
 * 
 */
@ApplicationScoped
public class RunFlyway {

    @ConfigProperty(name = "run.flyway.migrate-at-start")
    boolean runMigration;

    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    String datasourceUrl;
    @ConfigProperty(name = "quarkus.datasource.username")
    String datasourceUsername;
    @ConfigProperty(name = "quarkus.datasource.password")
    String datasourcePassword;

    public void runFlywayMigration(@Observes StartupEvent event) {
        if (runMigration) {
            Log.info(datasourceUsername+" "+datasourcePassword);
            Flyway flyway = Flyway.configure()
                    .dataSource("jdbc:" + datasourceUrl, datasourceUsername, datasourcePassword).load();
            flyway.migrate();
        }
    }
}