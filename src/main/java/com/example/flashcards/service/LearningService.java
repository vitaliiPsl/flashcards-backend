package com.example.flashcards.service;

import com.example.flashcards.dto.learning.QuestionDto;
import com.example.flashcards.dto.learning.QuestionAnswerDto;
import org.springframework.security.core.Authentication;

/**
 * Learning service
 */
public interface LearningService {

    /**
     * Submit answer to the question with provided id
     *
     * @param questionId id of the question
     * @param answer     provided answer
     * @param auth       authentication
     * @return result of the question
     */
    QuestionDto submitQuestionAnswer(long questionId, QuestionAnswerDto answer, Authentication auth);

    /**
     * Create question for set with given id
     *
     * @param setId       id of the set
     * @param questionDto initial question parameters
     * @param auth        authentication
     * @return created question
     */
    QuestionDto createQuestion(long setId, QuestionDto questionDto, Authentication auth);
}
