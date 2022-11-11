package com.example.flashcards.service;

import com.example.flashcards.dto.UserDto;
import org.springframework.security.core.Authentication;

/**
 * User service
 */
public interface UserService {

    /**
     * Fetch user with given id
     *
     * @param id id of the user
     * @return retrieved user
     */
    UserDto getUserById(long id);

    /**
     * Fetch authenticated user
     *
     * @param auth authentication
     * @return retrieved user
     */
    UserDto getAuthenticatedUser(Authentication auth);
}
