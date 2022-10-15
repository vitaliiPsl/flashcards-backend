package com.example.flashcards.controller;

import com.example.flashcards.dto.UserDto;
import com.example.flashcards.dto.requests.AuthRequest;
import com.example.flashcards.dto.responses.AuthResponse;
import com.example.flashcards.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public AuthResponse signIn(@Valid @RequestBody AuthRequest authRequest) {
        return authService.signIn(authRequest);
    }

    @PostMapping("/signup")
    public UserDto signIn(@Valid @RequestBody UserDto userDto) {
        return authService.signUp(userDto);
    }
}
