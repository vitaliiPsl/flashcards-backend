package com.example.flashcards.controller;

import com.example.flashcards.api.LearningApi;
import com.example.flashcards.dto.learning.QuestionAnswerDto;
import com.example.flashcards.dto.learning.QuestionDto;
import com.example.flashcards.service.LearningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LearningController implements LearningApi {
    private final LearningService learningService;

    public QuestionDto createQuestion(long setId, QuestionDto questionDto, Authentication auth) {
        return learningService.createQuestion(setId, questionDto, auth);
    }

    public QuestionDto submitAnswer(long questionId, QuestionAnswerDto answer, Authentication auth) {
        return learningService.submitQuestionAnswer(questionId, answer, auth);
    }
}
