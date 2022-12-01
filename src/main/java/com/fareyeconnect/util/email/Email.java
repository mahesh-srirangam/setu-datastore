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

package com.fareyeconnect.util.email;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email implements Serializable {

    private String from;//optional by default it will be sent from Team FarEye <team@getfareye.com>

    @NotBlank(message = "{field.to.empty}")
    private String to;//comma separated list of emails

    @NotBlank(message = "{field.subject.empty}")
    private String subject;//Staging will be prepended if env is not production

    private Map<String, Serializable> props;//placeholders in the template

    private String body;//only used locally consumer does not accept it.

    private String cc;//comma separated list of emails

    private String bcc;//not supported by FarEye yet

    private String toUserId;//comma separated list of userIds

    private String ccUserId;//comma separated list of userIds

    private List<AttachmentDto> attachment;//multiple attachment support

    private String attachmentFilename;//optional

    private String orgId;//set the org id if you want auth service to send org name in subject line

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttachmentDto implements Serializable {

        String filename;//name of file to be added it is optional
        String content;//content of the file
    }

    public Email(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}