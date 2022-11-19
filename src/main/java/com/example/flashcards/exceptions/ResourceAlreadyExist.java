package com.example.flashcards.exceptions;

public class ResourceAlreadyExist extends RuntimeException{

    public ResourceAlreadyExist(Object identifier, Class<?> type) {
        super(type.getSimpleName() + " with identifier: '" + identifier + "' already exist");
    }
}
