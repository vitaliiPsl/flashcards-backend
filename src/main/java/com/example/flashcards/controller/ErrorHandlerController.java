package com.example.flashcards.controller;

import com.example.flashcards.dto.errors.ApiError;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class ErrorHandlerController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleServerError(Exception e) {
        log.error("handleServerError: {}", e.getMessage(), e);

        String error = "Server error";
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(BadCredentialsException e) {
        log.error("handleAuthenticationException: {}", e.getMessage(), e);

        String error = "Invalid username or password";
        return buildResponseEntity(new ApiError(HttpStatus.FORBIDDEN, error, e));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadable: {}", e.getMessage(), e);

        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, e));
    }

    @ExceptionHandler(ResourceNotAccessible.class)
    protected ResponseEntity<ApiError> handleResourceNotAccessible(ResourceNotAccessible e) {
        log.error("handleResourceNotAccessible: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage(), e));
    }

    @ExceptionHandler(ResourceAlreadyExist.class)
    protected ResponseEntity<ApiError> handleResourceAlreadyExist(ResourceAlreadyExist e) {
        log.error("handleEntityAlreadyExistsException: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiError(BAD_REQUEST, e.getMessage(), e));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiError> handleMethodArgumentNotValid(BindException e) {
        log.error("handleMessageArgNotValid: {}", e.getMessage(), e);

        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.setDebugMessage(e.getMessage());
        apiError.addValidationErrors(e.getBindingResult().getFieldErrors());

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handleIllegalArgumentException: {}", e.getMessage(), e);

        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(e.getMessage());

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<ApiError> handleIllegalArgumentException(IllegalStateException e) {
        log.error("handleIllegalStateException: {}", e.getMessage(), e);

        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(e.getMessage());

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ResourceNotFound.class)
    protected ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFound e) {
        log.error("handleEntityNotFound: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiError(NOT_FOUND, e.getMessage(), e));
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
