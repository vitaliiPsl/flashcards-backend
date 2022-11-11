package com.example.flashcards.dto.learning;

import com.example.flashcards.model.learning.CardSide;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String question;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String answer;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> options;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean correct;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime answeredAt;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private CardSide cardSide = CardSide.BACK;
}
