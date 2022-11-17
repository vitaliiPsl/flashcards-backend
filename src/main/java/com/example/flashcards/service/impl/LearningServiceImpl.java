package com.example.flashcards.service.impl;

import com.example.flashcards.dto.learning.QuestionDto;
import com.example.flashcards.dto.learning.QuestionAnswerDto;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.*;
import com.example.flashcards.model.learning.CardSide;
import com.example.flashcards.model.learning.Difficulty;
import com.example.flashcards.model.learning.Question;
import com.example.flashcards.repository.CardSetRepository;
import com.example.flashcards.repository.QuestionRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.LearningService;
import com.example.flashcards.service.utils.DtoMappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class LearningServiceImpl implements LearningService {
    private final CardSetRepository setRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final DtoMappers mappers;

    private static final int NUMBER_OF_ANSWER_OPTIONS = 4;

    @Override
    public QuestionDto submitQuestionAnswer(long questionId, QuestionAnswerDto questionAnswerDto, Authentication auth) {
        log.info("Verify answer {} for question with id {}", questionAnswerDto, questionId);

        Question question = getQuestion(questionId);

        User user = getUser(auth);
        if (!question.getUser().equals(user)) {
            log.error("User {} doesn't have access to question with id {}", user.getId(), questionId);
            throw new ResourceNotAccessible(question, user, Question.class);
        }

        if (question.isClosed()) {
            log.error("Question with id {} is already closed", question);
            throw new IllegalStateException("Question is already closed");
        }

        return verifyAnswer(question, questionAnswerDto);
    }

    @Override
    public QuestionDto createQuestion(long setId, QuestionDto questionDto, Authentication auth) {
        log.info("Create study question for set with id: {}", setId);

        CardSet set = getSet(setId);
        User user = getUser(auth);

        if (!set.getAuthor().equals(user)) {
            log.error("User {} is not the author of the set {}", auth.getName(), setId);
            throw new ResourceNotAccessible(set, user, CardSet.class);
        }

        List<Card> cards = new ArrayList<>(set.getCards());
        if (cards.isEmpty()) {
            log.error("Set is empty");
            throw new IllegalStateException(String.format("Set %s is empty", setId));
        }

        Question question = buildQuestion(user, cards, questionDto.getCardSide());
        question = questionRepository.save(question);

        return mappers.mapQuestionToQuestionDto(question);
    }

    private QuestionDto verifyAnswer(Question question, QuestionAnswerDto questionAnswerDto) {
        String correctAnswer = question.getCorrectAnswer();
        String answer = questionAnswerDto.getAnswer();

        if (correctAnswer.equals(answer)) {
            incrementCardDifficulty(question.getCard());
            question.setCorrect(true);
        } else {
            decrementCardDifficulty(question.getCard());
            question.setCorrect(false);
        }

        question.setAnswer(answer);
        question.setAnsweredAt(LocalDateTime.now());

        return mappers.mapQuestionToQuestionDto(question);
    }

    private Question buildQuestion(User user, List<Card> cards, CardSide side) {
        Card card = selectCardToStudy(cards);
        List<Card> answerOptions = getAnswerOptions(cards, card);

        String cardQuestion = getQuestion(card, side);
        String correctAnswer = getCorrectAnswer(card, side);
        List<String> options = getOptions(answerOptions, side);

        return Question.builder()
                .user(user).card(card)
                .cardSide(side)
                .question(cardQuestion)
                .correctAnswer(correctAnswer)
                .options(options).build();
    }

    private String getQuestion(Card card, CardSide side) {
        return side == CardSide.BACK ? card.getBack() : card.getFront();
    }

    private String getCorrectAnswer(Card card, CardSide side) {
        return side == CardSide.BACK ? card.getFront() : card.getBack();
    }

    private List<String> getOptions(List<Card> cardOptions, CardSide side) {
        Function<Card, String> mapper = side == CardSide.BACK ? Card::getFront : Card::getBack;

        return cardOptions.stream().map(mapper).collect(Collectors.toList());
    }

    private Card selectCardToStudy(List<Card> cards) {
        Map<Difficulty, List<Card>> buckets = groupByProgress(cards);
        if (buckets.size() == 1) {
            return selectRandomCard(cards);
        }

        return selectRandomCard(cards);
    }

    private static Map<Difficulty, List<Card>> groupByProgress(List<Card> cards) {
        return cards.stream().collect(Collectors.groupingBy(Card::getDifficulty));
    }

    private List<Card> getAnswerOptions(List<Card> cards, Card card) {
        List<Card> options = new ArrayList<>();
        options.add(card);

        int size = Math.min(cards.size(), NUMBER_OF_ANSWER_OPTIONS);
        while (options.size() != size) {
            Card option = selectRandomCard(cards);

            if (!options.contains(option)) {
                options.add(option);
            }
        }

        Collections.shuffle(options);
        return options;
    }

    private static Card selectRandomCard(List<Card> cards) {
        return cards.get(new Random().nextInt(cards.size()));
    }

    private void incrementCardDifficulty(Card card) {
        Difficulty difficulty = card.getDifficulty();

        if (difficulty == Difficulty.HARD) {
            card.setDifficulty(Difficulty.GOOD);
        } else if(difficulty == Difficulty.GOOD) {
            card.setDifficulty(Difficulty.EASY);
        }
    }

    private void decrementCardDifficulty(Card card) {
        Difficulty difficulty = card.getDifficulty();

        if (difficulty == Difficulty.GOOD) {
            card.setDifficulty(Difficulty.HARD);
        } else if(difficulty == Difficulty.EASY) {
            card.setDifficulty(Difficulty.EASY);
        }
    }

    private Question getQuestion(long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFound(questionId, Question.class));
    }

    private CardSet getSet(long setId) {
        return setRepository.findById(setId)
                .orElseThrow(() -> new ResourceNotFound(setId, CardSet.class));
    }

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFound(auth.getName(), User.class));
    }
}
