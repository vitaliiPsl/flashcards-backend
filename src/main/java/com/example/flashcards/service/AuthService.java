package com.example.flashcards.service;

import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.dto.auth.AuthRequest;
import com.example.flashcards.dto.auth.AuthResponse;

/**
 * Authentication service
 */
public interface AuthService {

    /**
     * Authenticate the user with given credentials
     * @param authRequest credentials of the user
     * @return auth response that contains the JWT token
     */
    AuthResponse signIn(AuthRequest authRequest);

    /**
     * Register a new user
     * @param userDto user to register
     * @return registered user
     */
    UserDto signUp(UserDto userDto);
}
