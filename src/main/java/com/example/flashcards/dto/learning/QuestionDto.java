package com.example.flashcards.dto.learning;

import com.example.flashcards.model.learning.CardSide;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Id of the question", example = "4")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Question", example = "Bonjour")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String question;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Answer", description = "Answer provided by user", example = "Hello")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String answer;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Options", description = "Available options for answer", example = "['How are you?', 'Hello', 'Happy', 'Evening']")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> options;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Correct", description = "Specifies whenever answer is correct", example = "true")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean correct;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime answeredAt;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private CardSide cardSide = CardSide.BACK;
}
