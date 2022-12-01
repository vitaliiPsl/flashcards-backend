package com.example.flashcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "email", description = "Email address of the user",  example = "j.doe@mail.com", required = true)
    @Email(message = "Email address must be valid")
    @NotBlank(message = "Email address is required")
    private String email;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "password", description = "Password of the user",  example = "password", required = true)
    @NotBlank(message = "Password is required")
    private String password;
}
