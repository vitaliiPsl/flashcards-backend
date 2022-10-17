package com.example.flashcards.exceptions;

import com.example.flashcards.model.User;

public class ResourceNotAccessible extends RuntimeException {

    public ResourceNotAccessible(Object identifier, User user, Class<?> type) {
        super(String.format("User %s has no access to %s with identifier: '%s'",
                user.getId(), type.getSimpleName(), identifier));
    }
}
