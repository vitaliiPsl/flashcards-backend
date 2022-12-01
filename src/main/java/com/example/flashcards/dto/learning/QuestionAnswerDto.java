package com.example.flashcards.dto.learning;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionAnswerDto {
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Answer for the question", example = "Hello")
    private String answer;
}
