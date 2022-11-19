package com.example.flashcards.api;

import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.dto.auth.AuthRequest;
import com.example.flashcards.dto.auth.AuthResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/auth")
public interface AuthApi {

    @PostMapping("/signin")
    AuthResponse signIn(@Valid @RequestBody AuthRequest authRequest);

    @PostMapping("/signup")
    UserDto signIn(@Valid @RequestBody UserDto userDto);
}
