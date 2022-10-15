package com.example.flashcards.dto.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError extends ApiSubError {

    private String object;

    private String field;

    private Object rejectedValue;

    private String message;

    public ValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}
