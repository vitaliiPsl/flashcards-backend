package com.example.flashcards.controller;

import com.example.flashcards.api.UserApi;
import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController implements UserApi {
    private final UserService userService;

    public UserDto getUserById(long id) {
        return userService.getUserById(id);
    }

    public UserDto getAuthenticatedUser(Authentication auth) {
        return userService.getAuthenticatedUser(auth);
    }
}
