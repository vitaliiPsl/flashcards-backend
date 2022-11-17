package com.example.flashcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "jwt", description = "Jwt token")
    private String jwt;
}
