// /**
//  * ****************************************************************************
//  *
//  * Copyright (c) 2022, FarEye and/or its affiliates. All rights
//  * reserved.
//  * ___________________________________________________________________________________
//  *
//  *
//  * NOTICE: All information contained herein is, and remains the property of
//  * FaEye and its suppliers,if any. The intellectual and technical concepts
//  * contained herein are proprietary to FarEye. and its suppliers and
//  * may be covered by us and Foreign Patents, patents in process, and are
//  * protected by trade secret or copyright law. Dissemination of this information
//  * or reproduction of this material is strictly forbidden unless prior written
//  * permission is obtained from FarEye
//  */
// package com.fareyeconnect.exception;

// import javax.servlet.http.HttpServletRequest;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang.exception.ExceptionUtils;
// import org.hibernate.exception.ConstraintViolationException;
// import org.springframework.core.Ordered;
// import org.springframework.core.annotation.Order;
// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.converter.HttpMessageNotReadableException;
// import org.springframework.http.converter.HttpMessageNotWritableException;
// import org.springframework.web.HttpMediaTypeNotSupportedException;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.MissingServletRequestParameterException;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.context.request.ServletWebRequest;
// import org.springframework.web.context.request.WebRequest;
// import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
// import org.springframework.web.servlet.NoHandlerFoundException;
// import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// import static org.springframework.http.HttpStatus.BAD_REQUEST;
// import static org.springframework.http.HttpStatus.CONFLICT;
// import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
// import static org.springframework.http.HttpStatus.NOT_FOUND;
// import org.springframework.web.bind.annotation.ResponseStatus;

// /**
//  *
//  * @author Baldeep Singh Kwatra
//  * @since 14-May-2022, 5:38:50 PM
//  */
// @Order(Ordered.HIGHEST_PRECEDENCE)
// @ControllerAdvice
// @Slf4j
// public class RestExceptionHandler extends ResponseEntityExceptionHandler {

//     /**
//      * Handle MissingServletRequestParameterException. Triggered when a 'required'
//      * request parameter is missing.
//      *
//      * @param ex      MissingServletRequestParameterException
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     @ResponseStatus(HttpStatus.BAD_REQUEST)
//     protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
//             HttpHeaders headers, HttpStatus status, WebRequest request) {
//         String error = ex.getParameterName() + " parameter is missing";
//         return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
//     }

//     /**
//      * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is
//      * invalid as well.
//      *
//      * @param ex      HttpMediaTypeNotSupportedException
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//     protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
//             HttpHeaders headers, HttpStatus status, WebRequest request) {
//         StringBuilder builder = new StringBuilder();
//         builder.append(ex.getContentType());
//         builder.append(" media type is not supported. Supported media types are ");
//         ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
//         return buildResponseEntity(
//                 new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
//     }

//     /**
//      * Handle MethodArgumentNotValidException. Triggered when an object fails
//      *
//      * @Valid validation.
//      *
//      * @param ex      the MethodArgumentNotValidException that is thrown when @Valid
//      *                validation fails
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//             HttpHeaders headers, HttpStatus status, WebRequest request) {
//         ApiError apiError = new ApiError(BAD_REQUEST);
//         apiError.setMessage("Validation error");
//         apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
//         apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
//         return buildResponseEntity(apiError);
//     }

//     /**
//      * Handles javax.validation.ConstraintViolationException. Thrown when
//      *
//      * @Validated fails.
//      *
//      * @param ex the ConstraintViolationException
//      * @return the ApiError object
//      */
//     @ExceptionHandler(javax.validation.ConstraintViolationException.class)
//     @ResponseStatus(HttpStatus.BAD_REQUEST)
//     protected ResponseEntity<Object> handleConstraintViolation(javax.validation.ConstraintViolationException ex) {
//         ApiError apiError = new ApiError(BAD_REQUEST);
//         apiError.setMessage("Validation error");
//         apiError.addValidationErrors(ex.getConstraintViolations());
//         return buildResponseEntity(apiError);
//     }

//     /**
//      * Handles EntityNotFoundException.Created to encapsulate errors with more
//      * detail than javax.persistence.EntityNotFoundException.
//      *
//      * @param ex
//      * @return the ApiError object
//      */
//     @ExceptionHandler(EntityNotFoundException.class)
//     @ResponseStatus(HttpStatus.NOT_FOUND)
//     protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
//         ApiError apiError = new ApiError(NOT_FOUND);
//         apiError.setMessage(ex.getMessage());
//         return buildResponseEntity(apiError);
//     }

//     /**
//      * Handle HttpMessageNotReadableException. Happens when request JSON is
//      * malformed.
//      *
//      * @param ex      HttpMessageNotReadableException
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     @ResponseStatus(HttpStatus.BAD_REQUEST)
//     protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
//             HttpHeaders headers, HttpStatus status, WebRequest request) {
//         ServletWebRequest servletWebRequest = (ServletWebRequest) request;
//         log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
//         String error = "Malformed JSON request";
//         return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
//     }

//     /**
//      * Handle HttpMessageNotWritableException.
//      *
//      * @param ex      HttpMessageNotWritableException
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//     protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
//             HttpHeaders headers, HttpStatus status, WebRequest request) {
//         String error = "Error writing JSON output";
//         return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
//     }

//     /**
//      * Handle NoHandlerFoundException.
//      *
//      * @param ex
//      * @param headers
//      * @param status
//      * @param request
//      * @return
//      */
//     @Override
//     @ResponseStatus(HttpStatus.BAD_REQUEST)
//     protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
//             HttpStatus status, WebRequest request) {
//         ApiError apiError = new ApiError(BAD_REQUEST);
//         apiError.setMessage(
//                 String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
//         apiError.setDebugMessage(ex.getMessage());
//         return buildResponseEntity(apiError);
//     }

//     /**
//      * Handle javax.persistence.EntityNotFoundException
//      *
//      * @param ex
//      * @return
//      */
//     @ExceptionHandler(javax.persistence.EntityNotFoundException.class)
//     @ResponseStatus(HttpStatus.NOT_FOUND)
//     protected ResponseEntity<Object> handleEntityNotFound(javax.persistence.EntityNotFoundException ex) {
//         return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex));
//     }

//     /**
//      * Handle DataIntegrityViolationException, inspects the cause for different DB
//      * causes.
//      *
//      * @param ex the DataIntegrityViolationException
//      * @return the ApiError object
//      */
//     @ExceptionHandler(DataIntegrityViolationException.class)
//     @ResponseStatus(HttpStatus.CONFLICT)
//     protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
//         if (ex.getCause() instanceof ConstraintViolationException) {
//             ApiError apiError = new ApiError(CONFLICT);
//             apiError.setMessage("Database error");
//             apiError.setDebugMessage(ExceptionUtils.getStackTrace(ex));
//             return buildResponseEntity(apiError);
//         }
//         return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
//     }

//     /**
//      * Handle Exception, handle generic Exception.class
//      *
//      * @param ex the Exception
//      * @return the ApiError object
//      */
//     @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//     @ResponseStatus(HttpStatus.BAD_REQUEST)
//     protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
//         ApiError apiError = new ApiError(BAD_REQUEST);
//         Class<?> clazz = ex.getRequiredType();
//         if (clazz != null) {
//             apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
//                     ex.getName(), ex.getValue(), clazz.getSimpleName()));
//         }
//         apiError.setDebugMessage(ExceptionUtils.getStackTrace(ex));
//         return buildResponseEntity(apiError);
//     }

//     @ExceptionHandler(AppException.class)
//     @ResponseStatus(HttpStatus.BAD_REQUEST)
//     public ResponseEntity<Object> appExceptionHandler(Throwable ex, HttpServletRequest request) {
//         ApiError apiError = new ApiError(BAD_REQUEST);
//         apiError.setMessage(ex.getMessage());
//         apiError.setDebugMessage(ExceptionUtils.getStackTrace(ex));
//         return buildResponseEntity(apiError);
//     }

//     @ExceptionHandler(AppRetryException.class)
//     @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
//     public ResponseEntity<Object> retryExceptionHandler(Throwable ex, HttpServletRequest request) {
//         ApiError apiError = new ApiError(HttpStatus.TOO_MANY_REQUESTS);
//         apiError.setMessage(ex.getMessage());
//         apiError.setDebugMessage(ExceptionUtils.getStackTrace(ex));
//         return ResponseEntity.status(apiError.getStatus()).body(apiError);
//     }

//     @ExceptionHandler(Throwable.class)
//     public ResponseEntity<Object> handleThrowable(final Throwable ex) {
//         ApiError apiError = new ApiError(INTERNAL_SERVER_ERROR);
//         apiError.setMessage(ex.getMessage());
//         apiError.setDebugMessage(ExceptionUtils.getStackTrace(ex));
//         return buildResponseEntity(apiError);
//     }

//     private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
//         return new ResponseEntity<>(apiError, apiError.getReasonPhrase());
//     }

// }