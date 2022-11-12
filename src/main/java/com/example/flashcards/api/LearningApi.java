package com.example.flashcards.api;

import com.example.flashcards.dto.learning.QuestionAnswerDto;
import com.example.flashcards.dto.learning.QuestionDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/learning")
public interface LearningApi {

    @PostMapping(path = "questions", params = "setId")
    QuestionDto createQuestion(
            @RequestParam long setId,
            @RequestBody QuestionDto questionDto,
            Authentication auth
    );

    @PutMapping("questions/{questionId}")
    QuestionDto submitAnswer(
            @PathVariable long questionId,
            @RequestBody QuestionAnswerDto answer,
            Authentication auth
    );
}
