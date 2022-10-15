package com.example.flashcards.service;

import org.springframework.security.core.Authentication;

/**
 * JWT service
 */
public interface JwtService {

    /**
     * Build jwt token from the user authentication
     * @param authentication user authentication
     * @return jwt token
     */
    String createToken(Authentication authentication);

    /**
     * Decode jwt token
     * @param token jwt token
     * @return user authentication
     */
    Authentication decodeToken(String token);
}
