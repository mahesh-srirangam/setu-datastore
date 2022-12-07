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

import lombok.Data;

import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

// import org.eclipse.microprofile.config.inject.ConfigProperty;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.cloud.context.config.annotation.RefreshScope;
// import org.springframework.context.annotation.Configuration;

/**
 * Global Configuration for loading environment properties from Config Service
 *
 * @author Baldeep Singh Kwatra
 * @since 16-Nov-2022, 8:00:49 PM
 */
// @Configuration
@Singleton
// @RefreshScope
@Data
public class Property {

    // @ConfigProperty(name = "fareye.auth.default.user")
    // private String defaultUser;

    // @ConfigProperty(name = "fareye.auth.default.password")
    // private String defaultPassword;

    // @ConfigProperty(name = "fareye.auth.default.authority", defaultValue = "")
    // private String[] defaultAuthority;

    // @ConfigProperty(name = "fareye.auth.whitelist.url")
    // private String[] whitelistUrl;

    // @ConfigProperty(name = "spring.datasource.username")
    // private String dbUser;

    // @ConfigProperty(name = "spring.datasource.password")
    // private String dbPassword;

    // // @ConfigProperty(name = "jdbc:postgresql://${spring.datasource.url}/")
    // // private String dbUrl;

    // @ConfigProperty(name = "app.dbName")
    // private String dbName;

    // @ConfigProperty(name = "app.schemaName")
    // private String dbSchema;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    private String kafkaBootstrapServers;

    // @ConfigProperty(name = "spring.rabbitmq.host")
    // private String rabbitHost;

    // @ConfigProperty(name = "spring.rabbitmq.virtual-host")
    // private String rabbitVirtualHost;

    // @ConfigProperty(name = "spring.rabbitmq.username")
    // private String rabbitUsername;

    // @ConfigProperty(name = "spring.rabbitmq.password")
    // private String rabbitPassword;

    // @ConfigProperty(name = "fareye.app-queue",defaultValue = "app-queue")
    // private String appQueue;

    // @ConfigProperty(name = "fareye.app-exchange",defaultValue = "app-exchange")
    // private String appExchange;

    // @ConfigProperty(name = "fareye.app-routing-key",defaultValue = "app-routing-key")
    // private String appRoutingKey;

    // @ConfigProperty(name = "s3.accessKey")
    // private String s3AccessKey;

    // @ConfigProperty(name = "s3.secret-key")
    // private String s3SecretKey;

    // @ConfigProperty(name = "s3.bucket-name")
    // private String s3BucketName;

    // @ConfigProperty(name = "app.alert.emails", defaultValue = "baldeep.kwatra@getfareye.com")
    // private String alertEmails;

    // @ConfigProperty(name = "app.sftp.host", defaultValue = "127.0.0.1")
    // private String sftpHost;

    // @ConfigProperty(name = "app.sftp.port", defaultValue = "22")
    // private int sftpPort;

    // @ConfigProperty(name = "app.sftp.user", defaultValue = "baldeep")
    // private String sftpUser;

    // @ConfigProperty(name = "app.sftp.password", defaultValue = "user@123")
    // private String sftpPassword;

    // @ConfigProperty(name = "quarkus.application.name")
    // private String appName;

     @ConfigProperty(name = "spring.cloud.config.profile", defaultValue = "dev")
     private String environment;

    // @ConfigProperty(name = "fareye.inbound-request-topic")
    // private String inboundRequestTopic;

    // @ConfigProperty(name = "opentracing.jaeger.udp-sender.port", defaultValue = "6831")
    // private int jaegerUdpPort;

    // @ConfigProperty(name = "opentracing.jaeger.udp-sender.host", defaultValue = "localhost")
    // private String jaegerUdpHost;

    // @ConfigProperty(name = "opentracing.jaeger.log-spans", defaultValue = "false")
    // private boolean jaegerLogSpans;

    // @ConfigProperty(name = "encrypt-key")
    // private String encryptKey;

    // @ConfigProperty(name = "app.kafka.max-block.seconds", defaultValue = "60")
    // private int kafkaMaxBlockSeconds;

    // @ConfigProperty(name = "app-master.ratelimiter-url", defaultValue = "60")
    // private String ratelimiterURL;

}