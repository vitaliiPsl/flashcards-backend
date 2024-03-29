package com.example.flashcards.controller;

import com.example.flashcards.api.AuthApi;
import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.dto.auth.AuthRequest;
import com.example.flashcards.dto.auth.AuthResponse;
import com.example.flashcards.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;

    public AuthResponse signIn(AuthRequest authRequest) {
        return authService.signIn(authRequest);
    }

    public UserDto signIn(UserDto userDto) {
        return authService.signUp(userDto);
    }
}
