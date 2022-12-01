package com.example.flashcards.api;

import com.example.flashcards.dto.auth.AuthRequest;
import com.example.flashcards.dto.auth.AuthResponse;
import com.example.flashcards.dto.errors.ApiError;
import com.example.flashcards.dto.user.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Authentication API")
@RestController
@RequestMapping("api/auth")
public interface AuthApi {

    @Operation(summary = "Sign in", description = "Returns JWT token if credentials are valid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", description = "Invalid credentials", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @PostMapping("/signin")
    AuthResponse signIn(@Valid @RequestBody AuthRequest authRequest);

    @Operation(summary = "Sign up", description = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad request. Check response body", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @PostMapping("/signup")
    UserDto signIn(@Valid @RequestBody UserDto userDto);
}
