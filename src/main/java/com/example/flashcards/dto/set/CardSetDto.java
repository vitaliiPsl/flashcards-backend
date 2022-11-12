package com.example.flashcards.dto.set;

import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.dto.card.CardDto;
import com.example.flashcards.model.SetType;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class CardSetDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIncludeProperties({"id", "nickname"})
    private UserDto author;

    @NotBlank(message = "The name of the set is required")
    private String name;
    private String description;

    private SetType type = SetType.PUBLIC;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    private Set<CardDto> cards = new HashSet<>();
}
