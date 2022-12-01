package com.example.flashcards.dto.set;

import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.dto.card.CardDto;
import com.example.flashcards.model.SetType;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardSetDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Id of the set", example = "4")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Author", implementation = UserDto.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIncludeProperties({"id", "nickname"})
    private UserDto author;

    @Schema(title = "Name of the set", example = "Japanese", required = true)
    @NotBlank(message = "The name of the set is required")
    private String name;

    @Schema(title = "Description of the set", example = "Japanese vocabulary")
    private String description;

    @Schema(title = "Set visibility", example = "PUBLIC", enumAsRef = true, defaultValue = "PUBLIC")
    private SetType type = SetType.PUBLIC;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @Schema(title = "List of flash cards")
    private Set<CardDto> cards = new HashSet<>();
}
