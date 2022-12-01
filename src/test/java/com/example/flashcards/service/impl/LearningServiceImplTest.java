package com.example.flashcards.service.impl;

import com.example.flashcards.dto.learning.QuestionAnswerDto;
import com.example.flashcards.dto.learning.QuestionDto;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.Card;
import com.example.flashcards.model.User;
import com.example.flashcards.model.learning.CardSide;
import com.example.flashcards.model.learning.Difficulty;
import com.example.flashcards.model.learning.Question;
import com.example.flashcards.repository.CardSetRepository;
import com.example.flashcards.repository.QuestionRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.LearningService;
import com.example.flashcards.service.utils.DtoMappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LearningServiceImplTest {
    CardSetRepository cardSetRepository;
    UserRepository userRepository;
    QuestionRepository questionRepository;
    DtoMappers mappers;

    LearningService learningService;

    private final User user = User.builder().id(3L).email("j.doe@mail.com").nickname("j@d0e").build();

    @BeforeEach
    void init() {
        cardSetRepository = Mockito.mock(CardSetRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        questionRepository = Mockito.mock(QuestionRepository.class);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mappers = Mockito.spy(new DtoMappers(mapper));

        learningService = new LearningServiceImpl(cardSetRepository, userRepository, questionRepository, mappers);
    }

    @ParameterizedTest
    @MethodSource("correctAnswerDifficulties")
    void givenSubmitQuestionAnswer_whenAnswerIsCorrect_thenDecrementCardDifficulty(Difficulty currentDifficulty, Difficulty expectedDifficulty) {
        // given
        Card card = Card.builder().id(2L).front("Bonsoir").back("Good evening").difficulty(currentDifficulty).build();

        long questionId = 2;
        Question question = Question.builder().id(questionId).card(card).user(user)
                .question("Bonsoir").correctAnswer("Good evening").cardSide(CardSide.FRONT)
                .options(List.of("Good evening", "Hello", "How are you?", "They")).build();

        String answer = "Good evening";
        QuestionAnswerDto questionAnswerDto = QuestionAnswerDto.builder().answer(answer).build();

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null);

        // when
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userRepository.findByEmail(auth.getName())).thenReturn(Optional.of(user));

        QuestionDto result = learningService.submitQuestionAnswer(questionId, questionAnswerDto, auth);

        // then
        verify(questionRepository).findById(questionId);
        verify(userRepository).findByEmail(user.getEmail());
        verify(mappers).mapQuestionToQuestionDto(question);

        assertThat(result.getAnswer(), is(answer));
        assertThat(result.isCorrect(), is(true));
        assertThat(card.getDifficulty(), is(expectedDifficulty));
    }

    static Stream<Arguments> correctAnswerDifficulties() {
        return Stream.of(
                arguments(Difficulty.HARD, Difficulty.GOOD),
                arguments(Difficulty.GOOD, Difficulty.EASY),
                arguments(Difficulty.EASY, Difficulty.EASY)
        );
    }

    @ParameterizedTest
    @MethodSource("incorrectAnswerDifficulties")
    void givenSubmitQuestionAnswer_whenAnswerIsIncorrect_thenIncrementCardDifficulty(Difficulty currentDifficulty, Difficulty expectedDifficulty) {
        // given
        Card card = Card.builder().id(2L).front("Bonsoir").back("Good evening").difficulty(currentDifficulty).build();

        long questionId = 2;
        Question question = Question.builder().id(questionId).card(card).user(user)
                .question("Bonsoir").correctAnswer("Good evening").cardSide(CardSide.FRONT)
                .options(List.of("Good evening", "Hello", "How are you?", "They")).build();

        String answer = "Hello";
        QuestionAnswerDto questionAnswerDto = QuestionAnswerDto.builder().answer(answer).build();

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null);

        // when
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userRepository.findByEmail(auth.getName())).thenReturn(Optional.of(user));

        QuestionDto result = learningService.submitQuestionAnswer(questionId, questionAnswerDto, auth);

        // then
        verify(questionRepository).findById(questionId);
        verify(userRepository).findByEmail(user.getEmail());
        verify(mappers).mapQuestionToQuestionDto(question);

        assertThat(result.getAnswer(), is(answer));
        assertThat(result.isCorrect(), is(false));
        assertThat(card.getDifficulty(), is(expectedDifficulty));
    }

    static Stream<Arguments> incorrectAnswerDifficulties() {
        return Stream.of(
                arguments(Difficulty.HARD, Difficulty.HARD),
                arguments(Difficulty.GOOD, Difficulty.HARD),
                arguments(Difficulty.EASY, Difficulty.GOOD)
        );
    }

    @Test
    void givenSubmitQuestionAnswer_whenQuestionNotFound_thenThrowException() {
        // given
        long questionId = 2;

        String answer = "Hello";
        QuestionAnswerDto questionAnswerDto = QuestionAnswerDto.builder().answer(answer).build();

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null);

        // when
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFound.class, () -> learningService.submitQuestionAnswer(questionId, questionAnswerDto, auth));
    }

    @Test
    void givenSubmitQuestionAnswer_whenUserHasNoAccessToQuestion_thenThrowException() {
        // given
        long questionId = 2;
        Question question = Question.builder().id(questionId).user(user).build();

        String answer = "Hello";
        QuestionAnswerDto questionAnswerDto = QuestionAnswerDto.builder().answer(answer).build();

        User otherUser = User.builder().id(8).email("test@mail.com").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(otherUser.getEmail(), null);

        // when
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userRepository.findByEmail(auth.getName())).thenReturn(Optional.of(otherUser));

        // then
        assertThrows(ResourceNotAccessible.class, () -> learningService.submitQuestionAnswer(questionId, questionAnswerDto, auth));
    }

    @Test
    void givenSubmitQuestionAnswer_whenQuestionIsAlreadyClosed_thenThrowException() {
        // given
        long questionId = 2;
        Question question = Question.builder().id(questionId).user(user).answer("qwerty").build();

        String answer = "Test";
        QuestionAnswerDto questionAnswerDto = QuestionAnswerDto.builder().answer(answer).build();

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null);

        // when
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(userRepository.findByEmail(auth.getName())).thenReturn(Optional.of(user));

        // then
        assertThrows(IllegalStateException.class, () -> learningService.submitQuestionAnswer(questionId, questionAnswerDto, auth));
    }
}