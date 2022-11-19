package com.example.flashcards.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFound extends RuntimeException {

    public ResourceNotFound(Object identifier, Class<?> type) {
        super(type.getSimpleName() + " with identifier: '" + identifier + "' wasn't found");
    }
}
