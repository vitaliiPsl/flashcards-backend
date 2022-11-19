package com.example.flashcards.dto.errors;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiError {
    private HttpStatus status;

    private String message;

    private String debugMessage;

//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    private LocalDateTime timestamp = LocalDateTime.now();

    private List<ApiSubError> subErrors = new ArrayList<>();

    public ApiError(HttpStatus status) {
        this.status = status;
    }

    public ApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiError(HttpStatus status, Exception exception) {
        this.status = status;
        this.message = exception.getMessage();
        this.debugMessage = exception.getLocalizedMessage();
    }

    public ApiError(HttpStatus status, String message, Exception exception) {
        this.status = status;
        this.message = message;
        this.debugMessage = exception.getLocalizedMessage();
    }

    private void addSubError(ApiSubError error) {
        subErrors.add(error);
    }

    private void addValidationError(String object, String field, Object rejectedValue, String message) {
        addSubError(new ValidationError(object, field, rejectedValue, message));
    }

    private void addValidationError(String object, String message) {
        addSubError(new ValidationError(object, message));
    }

    private void addValidationError(FieldError fieldError) {
        this.addValidationError(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
    }

    public void addValidationErrors(List<FieldError> fieldErrors) {
        fieldErrors.forEach(this::addValidationError);
    }
}
