package com.example.flashcards.api;

import com.example.flashcards.dto.errors.ApiError;
import com.example.flashcards.dto.user.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users APi")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("api/users")
public interface UserApi {

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "User doesn't exist", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            )),
    })
    @GetMapping("{id}")
    UserDto getUserById(@PathVariable long id);

    @Operation(summary = "Get authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping("authenticated")
    UserDto getAuthenticatedUser(Authentication auth);
}
