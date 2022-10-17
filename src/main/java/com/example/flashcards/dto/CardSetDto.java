package com.example.flashcards.dto;

import com.example.flashcards.model.SetType;
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
    private UserDto author;

    @NotBlank(message = "The name of the set is required")
    private String name;
    private String description;

    private SetType type = SetType.PUBLIC;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt = LocalDateTime.now();

    private Set<CardDto> cards = new HashSet<>();
}