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
package com.fareyeconnect.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response.Status;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import org.hibernate.validator.internal.engine.path.PathImpl;
// import org.springframework.http.HttpStatus;
// import org.springframework.validation.FieldError;
// import org.springframework.validation.ObjectError;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Baldeep Singh Kwatra
 * @since 14-May-2022, 5:31:44 PM
 */
@Data
@RegisterForReflection
class ApiError {

    private String errorId;
    /**
     * The status property holds the operation call status.
     */
    private Status reasonPhrase;

    private int status;

    /**
     * The timestamp property holds the date-time instance of when the error
     * happened.
     */
    private long timestamp;

    /**
     * The message property holds a user-friendly message about the error.
     */
    private String message;

    /**
     * The debugMessage property holds a system message describing the error in
     * more detail.
     */
    private String debugMessage;

    private String category;

    // @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ApiSubError> errors;

    private ApiError() {
        timestamp = Instant.now().toEpochMilli();
        errorId = UUID.randomUUID().toString();
    }

    ApiError(int status) {
        this();
        this.status = status;
        this.reasonPhrase = Status.fromStatusCode(status);
    }

    ApiError(Status status) {
        this();
        this.reasonPhrase = status;
        this.status = status.getStatusCode();
    }

    ApiError(Status status, Throwable ex) {
        this();
        this.reasonPhrase = status;
        this.status = status.getStatusCode();
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    ApiError(Status status, String message, Throwable ex) {
        this();
        this.reasonPhrase = status;
        this.status = status.getStatusCode();
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }

    private void addSubError(ApiSubError subError) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(subError);
    }

    private void addValidationError(String object, String field, Object rejectedValue, String message) {
        addSubError(new ApiValidationError(object, field, rejectedValue, message));
    }

    private void addValidationError(String object, String message) {
        addSubError(new ApiValidationError(object, message));
    }

    // private void addValidationError(FieldError fieldError) {
    // this.addValidationError(
    // fieldError.getObjectName(),
    // AppUtil.camelToSnake(fieldError.getField()),
    // fieldError.getRejectedValue(),
    // fieldError.getDefaultMessage());
    // }

    // public void addValidationErrors(List<FieldError> fieldErrors) {
    // fieldErrors.forEach(this::addValidationError);
    // }

    // private void addValidationError(ObjectError objectError) {
    // this.addValidationError(
    // objectError.getObjectName(),
    // objectError.getDefaultMessage());
    // }

    // public void addValidationError(List<ObjectError> globalErrors) {
    // globalErrors.forEach(this::addValidationError);
    // }

    /**
     * Utility method for adding error of ConstraintViolation. Usually when a
     *
     * @Valid validation fails.
     *
     * @param cv the ConstraintViolation
     */
    private void addValidationError(ConstraintViolation<?> cv) {
        this.addValidationError(
                cv.getLeafBean().getClass().getSimpleName(),
                ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
                cv.getInvalidValue(),
                cv.getMessage());
    }

    public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
        constraintViolations.forEach(this::addValidationError);
    }

    public void setReasonPhrase(Status reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
        this.status = reasonPhrase.getStatusCode();
    }

    public void setStatus(int statusCode) {
        this.status = statusCode;
        this.reasonPhrase = Status.fromStatusCode(statusCode);
    }
}