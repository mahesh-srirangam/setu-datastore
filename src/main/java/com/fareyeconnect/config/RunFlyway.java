/**
 * ****************************************************************************
 * <p>
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 * <p>
 * <p>
 * NOTICE: All information contained herein is, and remains the property of
 * FaEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye
 */
package com.fareyeconnect.config;

import io.quarkus.arc.Arc;
import io.quarkus.flyway.runtime.FlywayContainer;
import io.quarkus.flyway.runtime.FlywayContainerProducer;
import io.quarkus.flyway.runtime.QuarkusPathLocationScanner;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;

import javax.enterprise.event.Observes;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */

@Startup
public class RunFlyway {

    @ConfigProperty(name = "run.flyway.migrate-at-start")
    boolean runMigration;

    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    String datasourceUrl;
    @ConfigProperty(name = "quarkus.datasource.username")
    String datasourceUsername;
    @ConfigProperty(name = "quarkus.datasource.password")
    String datasourcePassword;

    private String DB_MIGRATION = "/db/migration/";


    void onStart(@Observes StartupEvent event) {
        if (runMigration) {
            Set<String> migrationFiles = new HashSet<>();
            if (!ProfileManager.getLaunchMode().isDevOrTest()) {
                String migrationFileNames = ConfigProvider.getConfig().getValue("app.migration-files", String.class);
                List<String> files = Arrays.asList(migrationFileNames.split(","));
                files.forEach(file -> migrationFiles.add(DB_MIGRATION + file));
                migrationFiles.add("db/migration/V2022.10.15.11.00__init_ddl.sql");
                QuarkusPathLocationScanner.setApplicationMigrationFiles(migrationFiles);
            }
            DataSource dataSource = Flyway.configure()
                    .dataSource("jdbc:" + datasourceUrl, datasourceUsername, datasourcePassword)
                    .getDataSource();
            FlywayContainerProducer flywayProducer = Arc.container().instance(FlywayContainerProducer.class).get();
            FlywayContainer flywayContainer = flywayProducer.createFlyway(dataSource, "<default>", true, true);
            Flyway flyway = flywayContainer.getFlyway();
            flyway.repair();
            flyway.migrate();
        }
    }
}