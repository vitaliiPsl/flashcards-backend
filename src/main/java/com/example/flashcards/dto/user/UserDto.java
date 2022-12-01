package com.example.flashcards.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserDto")
public class UserDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Id of the user", example = "3")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Schema(title = "Nickname of the user", description = "Will be equal to email, if not set", example = "john.d0e", minLength = 3, maxLength = 20)
    @Size(min = 3, max = 20, message = "Length of the username must be between 3 and 20 symbols")
    private String nickname;

    @Schema(title = "Email of the user", example = "john.doe@mail.com", required = true)
    @NotBlank(message = "Email address is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Password", example = "password", required = true, minLength = 8)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Minimal number of symbols in password is 8")
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
