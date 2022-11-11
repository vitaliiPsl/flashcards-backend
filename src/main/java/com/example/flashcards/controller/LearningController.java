package com.example.flashcards.controller;

import com.example.flashcards.dto.learning.QuestionDto;
import com.example.flashcards.dto.learning.QuestionAnswerDto;
import com.example.flashcards.service.LearningService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/learning")
public class LearningController {
    private final LearningService learningService;

    @PostMapping(path = "questions", params = "setId")
    public QuestionDto createQuestion(
            @RequestParam long setId,
            @RequestBody QuestionDto questionDto,
            Authentication auth
    ) {
        return learningService.createQuestion(setId, questionDto, auth);
    }

    @PutMapping("questions/{questionId}")
    public QuestionDto submitAnswer(
            @PathVariable long questionId,
            @RequestBody QuestionAnswerDto answer,
            Authentication auth
    ) {
        return learningService.submitQuestionAnswer(questionId, answer, auth);
    }
}
