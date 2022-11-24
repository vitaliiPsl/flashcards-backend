package com.example.flashcards.service.impl;

import com.example.flashcards.dto.card.CardDto;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.Card;
import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.SetType;
import com.example.flashcards.model.User;
import com.example.flashcards.model.learning.Difficulty;
import com.example.flashcards.repository.CardRepository;
import com.example.flashcards.repository.CardSetRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.utils.DtoMappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {
    @Mock
    CardRepository cardRepository;

    @Mock
    CardSetRepository setRepository;

    @Mock
    UserRepository userRepository;

    DtoMappers mappers;

    CardServiceImpl cardService;

    @BeforeEach
    void init() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        mappers = Mockito.spy(new DtoMappers(modelMapper));
        cardService = new CardServiceImpl(cardRepository, setRepository, userRepository, mappers);
    }

    @Test
    void givenSaveCard_whenCardIsValid_thenSaveCard() {
        // given
        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();
        Card card = Card.builder().front("Bonsoir").back("Good evening").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).cards(new HashSet<>()).build();

        // when
        when(mappers.mapCardDtoToCard(cardDto)).thenReturn(card);
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(cardRepository.save(any(Card.class))).then(returnsFirstArg());

        CardDto result = cardService.saveCard(setId, cardDto, auth);

        // then
        verify(mappers).mapCardDtoToCard(cardDto);
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
        verify(cardRepository).save(any(Card.class));
        verify(mappers).mapCardToCardDto(any(Card.class));

        assertThat(card.getSet(), is(set));
        assertThat(card.getDifficulty(), is(Difficulty.HARD));
        assertThat(result.getFront(), is(cardDto.getFront()));
        assertThat(result.getBack(), is(cardDto.getBack()));
        assertThat(result.getCreatedAt(), is(notNullValue()));
    }

    @Test
    void givenSaveCard_whenUserIsNotAuthorOfTheSet_thenThrowException() {
        // given
        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French")
                .author(User.builder().id(6L).email("test@mail.com").build()).cards(new HashSet<>()).build();

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));

        // then
        assertThrows(ResourceNotAccessible.class, () -> cardService.saveCard(setId, cardDto, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void givenSaveCard_whenSetNotFound_thenThrowException() {
        // given
        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(setRepository.findById(setId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFound.class, () -> cardService.saveCard(setId, cardDto, auth));
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
    }


    @Test
    void givenSaveCard_whenCardWithGivenFrontSideAlreadyExistInSet_thenSaveCard() {
        // given
        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).build();
        set.setCards(Set.of(Card.builder().front("Bonsoir").set(set).build()));

        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();
        Card card = Card.builder().front("Bonsoir").back("Good evening").build();

        // when
        when(mappers.mapCardDtoToCard(cardDto)).thenReturn(card);
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));

        // then
        assertThrows(ResourceAlreadyExist.class, () -> cardService.saveCard(setId, cardDto, auth));
        verify(mappers).mapCardDtoToCard(cardDto);
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void givenDeleteCard_thenDeleteCard() {
        // given
        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).build();

        long cardId = 2;
        Card card = Card.builder().id(cardId).set(set).front("Bonsoir").build();

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        cardService.deleteCard(cardId, setId, auth);

        // then
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
        verify(cardRepository).findById(cardId);
        verify(cardRepository).delete(card);
    }


    @Test
    void givenDeleteCard_whenCardDoesntBelongToGivenSet_thenThrowException() {
        // given
        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).build();

        long cardId = 2;
        Card card = Card.builder().id(cardId).front("Bonsoir")
                .set(CardSet.builder().id(1L).name("Test").author(author).build()).build();

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // then
        assertThrows(ResourceNotAccessible.class, () -> cardService.deleteCard(cardId, setId, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void givenDeleteCard_whenUserIsNotAuthorOfTheSet_thenDeleteCard() {
        // given
        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French")
                .author(User.builder().id(6L).email("test@mail.com").build()).cards(new HashSet<>()).build();

        long cardId = 2;

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));

        // then
        assertThrows(ResourceNotAccessible.class, () -> cardService.deleteCard(cardId, setId, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void givenDeleteCard_whenSetNotFound() {
        // given
        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        long cardId = 2;

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));

        // then
        assertThrows(ResourceNotFound.class, () -> cardService.deleteCard(cardId, setId, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void givenReplaceCard_whenReplacementIsValid_thenReplaceSet() {
        // given
        long cardId = 2;
        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();
        Card card = Card.builder().id(cardId).front("Test").back("Test").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).cards(Set.of(card)).build();

        card.setSet(set);

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).then(returnsFirstArg());

        CardDto result = cardService.replaceCard(cardId, setId, cardDto, auth);

        // then
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
        verify(cardRepository).save(card);
        verify(mappers).mapCardToCardDto(any(Card.class));

        assertThat(card.getSet(), is(set));
        assertThat(result.getFront(), is(cardDto.getFront()));
        assertThat(result.getBack(), is(cardDto.getBack()));
        assertThat(result.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    void givenReplaceCard_whenReplacementFrontValueIsAlreadyTakenByOtherCardInThatSet_thenThrowException() {
        // given
        long cardId = 2;
        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();
        Card card = Card.builder().id(cardId).front("Test").back("Test").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author)
                .cards(Set.of(card, Card.builder().id(9L).front("Bonsoir").build())).build();

        card.setSet(set);

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // then
        assertThrows(ResourceAlreadyExist.class, () -> cardService.replaceCard(cardId, setId, cardDto, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void givenReplaceCard_whenCardDoesntBelongToSetWithGivenId_thenThrowException() {
        // given
        long cardId = 2;
        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();
        Card card = Card.builder().id(cardId).front("Test").back("Test").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).cards(Set.of()).build();

        CardSet otherSet = CardSet.builder().id(2L).name("Spanish").author(author).cards(Set.of(card)).build();
        card.setSet(otherSet);

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // then
        assertThrows(ResourceNotAccessible.class, () -> cardService.replaceCard(cardId, setId, cardDto, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void givenReplaceCard_whenCardDoesntExist_thenThrowException() {
        // given
        long cardId = 2;
        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).cards(Set.of()).build();

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFound.class, () -> cardService.replaceCard(cardId, setId, cardDto, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void givenReplaceCard_whenSetDoesntExist_thenThrowException() {
        // given
        long cardId = 2;
        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));

        // then
        assertThrows(ResourceNotFound.class, () -> cardService.replaceCard(cardId, setId, cardDto, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void givenReplaceCard_whenUserIsNotAuthorOfTheSet_thenThrowException() {
        // given
        long cardId = 2;
        CardDto cardDto = CardDto.builder().front("Bonsoir").back("Good evening").build();

        String email = "jhn.doe@mail.com";
        User user = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        User author = User.builder().id(16L).email("test@mail.com").build();

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).cards(Set.of()).build();

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // then
        assertThrows(ResourceNotAccessible.class, () -> cardService.replaceCard(cardId, setId, cardDto, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void givenGetCardById_thenReturnCard() {
        // given
        long cardId = 2;
        Card card = Card.builder().id(cardId).front("Test").back("Test").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).cards(Set.of(card))
                .type(SetType.PUBLIC).build();

        card.setSet(set);

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        CardDto result = cardService.getCardById(cardId, setId, auth);

        // then
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
        verify(cardRepository).findById(cardId);
        verify(mappers).mapCardToCardDto(card);

        assertThat(result.getId(), is(cardId));
        assertThat(result.getFront(), is(card.getFront()));
        assertThat(result.getBack(), is(card.getBack()));
        assertThat(result.getCreatedAt(), is(card.getCreatedAt()));
    }

    @Test
    void givenGetCardById_whenUserIsNotSetAuthorAndTypeIsPrivate_thenThrowException() {
        // given
        long cardId = 2;
        Card card = Card.builder().id(cardId).front("Test").back("Test").build();

        String email = "jhn.doe@mail.com";
        User user = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        User author = User.builder().id(1L).email("test@mail").build();

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).cards(Set.of(card))
                .type(SetType.PRIVATE).build();

        card.setSet(set);

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));


        // then
        assertThrows(ResourceNotAccessible.class, () -> cardService.getCardById(cardId, setId, auth));
        verify(setRepository).findById(setId);
        verify(userRepository).findByEmail(email);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void givenGetCardById_whenCardDoesntBelongToGivenSet_thenThrowException() {
        // given
        long cardId = 2;
        Card card = Card.builder().id(cardId).front("Test").back("Test").build();

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).cards(Set.of()).build();

        CardSet other = CardSet.builder().id(2L).name("Spanish").author(author).cards(Set.of(card)).build();
        card.setSet(other);

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));


        // then
        assertThrows(ResourceNotAccessible.class, () -> cardService.getCardById(cardId, setId, auth));
        verify(setRepository).findById(setId);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void givenGetCardById_whenSetDoesntExist_thenThrowException() {
        // given
        long cardId = 2;

        String email = "jhn.doe@mail.com";
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.empty());


        // then
        assertThrows(ResourceNotFound.class, () -> cardService.getCardById(cardId, setId, auth));
        verify(setRepository).findById(setId);
    }

    @Test
    void givenGetCardById_whenCardDoesntExist_thenThrowException() {
        // given
        long cardId = 2;

        String email = "jhn.doe@mail.com";
        User author = User.builder().id(3L).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        CardSet set = CardSet.builder().id(setId).name("French").author(author).cards(Set.of()).build();

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());


        // then
        assertThrows(ResourceNotFound.class, () -> cardService.getCardById(cardId, setId, auth));
        verify(setRepository).findById(setId);
        verify(cardRepository).findById(cardId);
    }
}