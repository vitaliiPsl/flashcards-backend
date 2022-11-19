package com.example.flashcards.api;

import com.example.flashcards.dto.user.UserDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("api/users")
public interface UserApi {

    @GetMapping("{id}")
    UserDto getUserById(@PathVariable long id);

    @GetMapping("authenticated")
    UserDto getAuthenticatedUser(Authentication auth);
}
