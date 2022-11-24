package com.example.flashcards.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Id of the card", example = "15")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Schema(title = "Front", description = "Front side of the card", example = "Bonjour", required = true)
    @NotBlank(message = "Front side of the card is required")
    private String front;

    @Schema(title = "Back", description = "Back side of the card", example = "Hello")
    private String back;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
