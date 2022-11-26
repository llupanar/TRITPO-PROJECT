package com.explosion204.wclookup.controller;

import com.explosion204.wclookup.controller.util.ErrorResponseUtil;
import com.explosion204.wclookup.exception.EntityAlreadyExistsException;
import com.explosion204.wclookup.exception.EntityException;
import com.explosion204.wclookup.exception.EntityNotFoundException;
import com.explosion204.wclookup.exception.InvalidPageContextException;
import com.explosion204.wclookup.controller.debug.DebugMailLogger;
import com.explosion204.wclookup.exception.MailingException;
import com.explosion204.wclookup.service.MessageSourceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
public class ApplicationExceptionHandler {
    private static final String ERROR_DELIMITER = "; ";
    private static final String ENTITY_NOT_FOUND_MESSAGE = "entity_not_found";
    private static final String ENTITY_ALREADY_EXISTS_MESSAGE = "entity_already_exists";
    private static final String INVALID_ENTITY_MESSAGE = "invalid_entity";
    private static final String INVALID_CREDENTIALS_MESSAGE = "invalid_credentials";
    private static final String INVALID_PAGE_NUMBER_MESSAGE = "invalid_page_number";
    private static final String INVALID_PAGE_SIZE_MESSAGE = "invalid_page_size";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "internal_server_error";
    private static final String ACCESS_DENIED_MESSAGE = "access_denied";
    private static final String INVALID_REQUEST_FORMAT_MESSAGE = "invalid_request_format";
    private static final String MAILING_ERROR_MESSAGE = "mailing_error";

    private static final Logger logger = LogManager.getLogger();

    private final MessageSourceService messageSourceService;
    private final ErrorResponseUtil errorResponseUtil;
    private DebugMailLogger debugMailLogger;

    public ApplicationExceptionHandler(MessageSourceService messageSourceService, ErrorResponseUtil errorResponseUtil) {
        this.messageSourceService = messageSourceService;
        this.errorResponseUtil = errorResponseUtil;
    }

    @Autowired(required = false)
    public void setMailLogger(DebugMailLogger debugMailLogger) {
        this.debugMailLogger = debugMailLogger;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied() {
        String errorMessage = messageSourceService.getString(ACCESS_DENIED_MESSAGE);
        return errorResponseUtil.buildErrorResponseEntity(FORBIDDEN, errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(EntityNotFoundException e) {
        String causeEntityName = e.getCauseEntity().getSimpleName();
        String errorMessage = String.format(messageSourceService.getString(ENTITY_NOT_FOUND_MESSAGE), causeEntityName);
        return errorResponseUtil.buildErrorResponseEntity(NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> handleEntityAlreadyExists(EntityException e) {
        String causeEntityName = e.getCauseEntity().getSimpleName();
        String errorMessage = String.format(messageSourceService.getString(ENTITY_ALREADY_EXISTS_MESSAGE), causeEntityName);
        return errorResponseUtil.buildErrorResponseEntity(CONFLICT, errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e) {
        List<String> violations = e.getConstraintViolations()
                        .stream().map(ConstraintViolation::getMessage)
                        .toList();
        String violationsString = String.join(ERROR_DELIMITER, violations);

        String errorMessage = String.format(messageSourceService.getString(INVALID_ENTITY_MESSAGE), violationsString);
        return errorResponseUtil.buildErrorResponseEntity(BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler({ BadCredentialsException.class, CredentialsExpiredException.class })
    public ResponseEntity<Object> handleAuthError() {
        String errorMessage = messageSourceService.getString(INVALID_CREDENTIALS_MESSAGE);
        return errorResponseUtil.buildErrorResponseEntity(UNAUTHORIZED, errorMessage);
    }

    @ExceptionHandler(InvalidPageContextException.class)
    public ResponseEntity<Object> handleInvalidPageContext(InvalidPageContextException e) {
        InvalidPageContextException.ErrorType errorType = e.getErrorType();
        int invalidValue = e.getInvalidValue();

        String errorMessage = switch (errorType) {
            case INVALID_PAGE_NUMBER -> messageSourceService.getString(INVALID_PAGE_NUMBER_MESSAGE);
            case INVALID_PAGE_SIZE -> messageSourceService.getString(INVALID_PAGE_SIZE_MESSAGE);
        };

        return errorResponseUtil.buildErrorResponseEntity(BAD_REQUEST, String.format(errorMessage, invalidValue));
    }

    @ExceptionHandler(value = {
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<Object> handleBadRequest(Throwable e) {
        logger.error("Invalid request: {}", e.getMessage());
        String errorMessage = messageSourceService.getString(INVALID_REQUEST_FORMAT_MESSAGE);
        return errorResponseUtil.buildErrorResponseEntity(BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(MailingException.class)
    public ResponseEntity<Object> handleMailingException(MailingException e) {
        logger.error("Unable to send email", e);
        String errorMessage = messageSourceService.getString(MAILING_ERROR_MESSAGE);
        return errorResponseUtil.buildErrorResponseEntity(INTERNAL_SERVER_ERROR, errorMessage);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleDefault(HttpServletRequest request, Exception e) {
        logger.error("Uncaught exception", e);

        if (debugMailLogger != null) {
            debugMailLogger.log(request, e);
        }

        String errorMessage = messageSourceService.getString(INTERNAL_SERVER_ERROR_MESSAGE);
        return errorResponseUtil.buildErrorResponseEntity(INTERNAL_SERVER_ERROR, errorMessage);
    }
}
