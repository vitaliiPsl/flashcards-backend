package com.example.flashcards.exceptions;

import com.example.flashcards.model.User;

public class ResourceNotAccessible extends RuntimeException {

    public ResourceNotAccessible(Object identifier, User user, Class<?> type) {
        super(String.format("User %s has no access to resource %s with identifier: '%s'",
                user.getId(), type.getSimpleName(), identifier));
    }

    public ResourceNotAccessible(Object identifier, Class<?> type) {
        super(String.format("Resource %s is with identifier %s is no accessible for unauthorised users", type.getSimpleName(), identifier));
    }
}
