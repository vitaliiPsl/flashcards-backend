package com.example.flashcards.service.impl;

import com.example.flashcards.dto.card.CardDto;
import com.example.flashcards.dto.pagination.PaginationRequest;
import com.example.flashcards.dto.pagination.PaginationResponse;
import com.example.flashcards.dto.set.CardSetDto;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.Card;
import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.SetType;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.CardSetRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.utils.DtoMappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardSetServiceImplTest {

    CardSetRepository setRepository;
    UserRepository userRepository;
    DtoMappers mappers;

    CardSetServiceImpl setService;

    @BeforeEach
    void init() {
        setRepository = Mockito.mock(CardSetRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mappers = Mockito.spy(new DtoMappers(modelMapper));

        setService = new CardSetServiceImpl(setRepository, userRepository, mappers);
    }

    @Test
    void givenSaveSet_thenSaveSet() {
        // given
        Set<CardDto> cardDtos = Set.of(
                CardDto.builder().front("S'il vous plait").back("Please").build(),
                CardDto.builder().front("Et toi?").back("And you?").build()
        );

        CardSetDto setDto = CardSetDto.builder().name("French").description("French vocab")
                .cards(cardDtos).type(SetType.PRIVATE).build();

        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findByNameAndAuthor(setDto.getName(), user)).thenReturn(Optional.empty());
        when(setRepository.save(any(CardSet.class))).then(returnsFirstArg());

        CardSetDto result = setService.saveSet(setDto, auth);

        // then
        verify(userRepository).findByEmail(email);
        verify(setRepository).findByNameAndAuthor(setDto.getName(), user);
        verify(mappers).mapCardSetDtoToCardSet(setDto);
        verify(mappers, times(cardDtos.size())).mapCardDtoToCard(any(CardDto.class));
        verify(setRepository).save(any(CardSet.class));
        verify(mappers).mapCardSetToCardSetDto(any(CardSet.class));

        assertThat(result.getName(), is(setDto.getName()));
        assertThat(result.getDescription(), is(setDto.getDescription()));
        assertThat(result.getType(), is(setDto.getType()));
        assertThat(result.getAuthor().getId(), is(user.getId()));
        assertThat(result.getCreatedAt(), is(notNullValue()));
        assertThat(result.getCards(), hasSize(cardDtos.size()));
        assertTrue(result.getCards().stream().allMatch(cardDto -> cardDto.getCreatedAt() != null));
    }

    @Test
    void givenSaveSet_whenUserAlreadyHasSetWithGivenName_thenThrowException() {
        // given
        CardSetDto setDto = CardSetDto.builder().name("French").build();

        CardSet other = CardSet.builder().name("French").build();

        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findByNameAndAuthor(setDto.getName(), user)).thenReturn(Optional.of(other));

        // then
        assertThrows(ResourceAlreadyExist.class, () -> setService.saveSet(setDto, auth));

        verify(userRepository).findByEmail(email);
        verify(setRepository).findByNameAndAuthor(setDto.getName(), user);
    }

    @Test
    void givenDeleteSet_thenDeleteSet() {
        // given
        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 14;
        CardSet set = CardSet.builder().id(setId).name("French").author(user).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        setService.deleteSet(setId, auth);

        // then
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
        verify(setRepository).delete(set);
    }

    @Test
    void givenDeleteSet_whenSetDoesntExist_thenThrowException() {
        // given
        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 14;

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFound.class, () -> setService.deleteSet(setId, auth));
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
    }

    @Test
    void givenDeleteSet_whenUserIsNotAuthorOfTheSet_thenThrowException() {
        // given
        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        User other = User.builder().id(6).email("test@mail.com").build();

        long setId = 14;
        CardSet set = CardSet.builder().id(setId).name("French").author(other).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        // then
        assertThrows(ResourceNotAccessible.class, () -> setService.deleteSet(setId, auth));
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
    }

    @Test
    void givenReplaceSet_thenReplaceSet() {
        // given
        long setId = 4;
        CardSetDto setDto = CardSetDto.builder().name("Spanish").description("Spanish vocab").
                type(SetType.PRIVATE).build();

        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        CardSet set = CardSet.builder().id(setId).name("French").description("French vocab")
                .type(SetType.PUBLIC).author(user).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        CardSetDto result = setService.replaceSet(setId, setDto, auth);

        // then
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
        verify(setRepository).save(set);
        verify(mappers).mapCardSetToCardSetDto(set);

        assertThat(result.getName(), is(setDto.getName()));
        assertThat(result.getDescription(), is(setDto.getDescription()));
        assertThat(result.getType(), is(setDto.getType()));
    }

    @Test
    void givenReplaceSet_whenReplacementHasNewCards_thenShouldntSaveThemOrModifyExistingCards() {
        // given
        Set<CardDto> cardDtos = Set.of(
                CardDto.builder().front("S'il vous plait").back("Please").build(),
                CardDto.builder().front("Et toi?").back("And you?").build()
        );

        CardSetDto setDto = CardSetDto.builder().name("Spanish").description("Spanish vocab")
                .cards(cardDtos).type(SetType.PRIVATE).build();

        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 4;
        Set<Card> existingCards = Set.of(
                Card.builder().front("Salut").back("Hi").build(),
                Card.builder().front("Bonsoir").back("Good evening").build()
        );
        CardSet set = CardSet.builder().id(setId).name("French").description("French vocab")
                .cards(existingCards).type(SetType.PUBLIC).author(user).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        setService.replaceSet(setId, setDto, auth);

        // then
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
        verify(setRepository).save(set);
        verify(mappers).mapCardSetToCardSetDto(set);

        assertThat(set.getCards(), equalTo(existingCards));
    }

    @Test
    void givenReplaceSet_whenSetDoesntExist_thenThrowException() {
        // given
        long setId = 4;
        CardSetDto setDto = CardSetDto.builder().name("Spanish").description("Spanish vocab").
                type(SetType.PRIVATE).build();

        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFound.class, () -> setService.replaceSet(setId, setDto, auth));
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
    }

    @Test
    void givenReplaceSet_whenUserIsNotAuthorOfTheSet_thenThrowException() {
        // given
        CardSetDto setDto = CardSetDto.builder().name("Spanish").description("Spanish vocab").
                type(SetType.PRIVATE).build();

        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        User other = User.builder().id(6).email("test@mail.com").build();

        long setId = 14;
        CardSet set = CardSet.builder().id(setId).name("French").author(other).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        // then
        assertThrows(ResourceNotAccessible.class, () -> setService.replaceSet(setId, setDto, auth));
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
    }

    @Test
    void givenGetSetById_whenSetExist_thenReturnSet() {
        // given
        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 14;
        CardSet set = CardSet.builder().id(setId).name("French").author(user).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        CardSetDto result = setService.getSetById(setId, auth);

        // then
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
        verify(mappers).mapCardSetToCardSetDto(set);

        assertThat(result.getId(), is(setId));
        assertThat(result.getName(), is(set.getName()));
        assertThat(result.getAuthor().getEmail(), is(email));
    }


    @Test
    void givenGetSetById_whenSetDoesntExist_thenThrowException() {
        // given
        String email = "j.doe@mail.com";
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        long setId = 14;

        // when
        when(setRepository.findById(setId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFound.class, () -> setService.getSetById(setId, auth));
        verify(setRepository).findById(setId);
    }

    @Test
    void givenGetSetById_whenUserIsNotAuthorOfTheSetButSetIsPublic_thenReturnSet() {
        // given
        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        User other = User.builder().id(5).email("test@mail.com").build();

        long setId = 14;
        CardSet set = CardSet.builder().id(setId).name("French").author(other).type(SetType.PUBLIC).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        CardSetDto result = setService.getSetById(setId, auth);

        // then
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
        verify(mappers).mapCardSetToCardSetDto(set);

        assertThat(result.getId(), is(setId));
        assertThat(result.getName(), is(set.getName()));
        assertThat(result.getType(), is(SetType.PUBLIC));
        assertThat(result.getAuthor().getEmail(), is(other.getEmail()));
    }

    @Test
    void givenGetSetById_whenUserIsNotAuthorOfTheSetAndSetIsPrivate_thenThrowException() {
        // given
        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        User other = User.builder().id(5).email("test@mail.com").build();

        long setId = 14;
        CardSet set = CardSet.builder().id(setId).name("French").author(other).type(SetType.PRIVATE).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        // then
        assertThrows(ResourceNotAccessible.class, () -> setService.getSetById(setId, auth));
        verify(userRepository).findByEmail(email);
        verify(setRepository).findById(setId);
    }

    @Test
    void getSetsByAuthor_whenUserIsTheAuthor_thenReturnAllSets() {
        // given
        long userId = 2;
        String email = "j.doe@mail.com";
        User user = User.builder().id(userId).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        List<CardSet> sets = List.of(
                CardSet.builder().id(1L).author(user).name("French").type(SetType.PRIVATE).build(),
                CardSet.builder().id(2L).author(user).name("Spanish").type(SetType.PUBLIC).build(),
                CardSet.builder().id(3L).author(user).name("Japanese").type(SetType.PRIVATE).build()
        );

        Page<CardSet> page = new PageImpl<>(sets);

        PaginationRequest pagination = PaginationRequest.builder().page(0).size(10).build();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize());

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findByAuthor(user, pageRequest)).thenReturn(page);

        PaginationResponse<CardSetDto> response = setService.getSetsByAuthor(userId, pagination, auth);

        // then
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(email);
        verify(setRepository).findByAuthor(user, pageRequest);

        assertThat(response.getContent(), hasSize(sets.size()));
        assertThat(response.getPage(), is(pagination.getPage()));
        assertThat(response.getSize(), is(sets.size()));
    }

    @Test
    void getSetsByAuthor_whenUserIsNotTheAuthor_thenReturnPublicSets() {
        // given
        long userId = 2;
        User other = User.builder().id(userId).email("test@mail.com").build();

        String email = "j.doe@mail.com";
        User user = User.builder().id(3).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        List<CardSet> sets = List.of(
                CardSet.builder().id(2L).author(other).name("Spanish").type(SetType.PUBLIC).build()
        );

        Page<CardSet> page = new PageImpl<>(sets);

        PaginationRequest pagination = PaginationRequest.builder().page(0).size(10).build();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize());

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(other));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findByAuthorAndType(other, SetType.PUBLIC, pageRequest)).thenReturn(page);

        PaginationResponse<CardSetDto> response = setService.getSetsByAuthor(userId, pagination, auth);

        // then
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(email);
        verify(setRepository).findByAuthorAndType(other, SetType.PUBLIC, pageRequest);

        assertThat(response.getContent(), hasSize(sets.size()));
        assertThat(response.getPage(), is(pagination.getPage()));
        assertThat(response.getSize(), is(sets.size()));
    }

    @Test
    void givenGetPublicSetsByName_thenReturnPublicSetsWithGivenName() {
        // given
        String name = "fre";

        List<CardSet> sets = List.of(
                CardSet.builder().id(1L).name("French").type(SetType.PUBLIC).build(),
                CardSet.builder().id(2L).name("French B1 vocab").type(SetType.PUBLIC).build()
        );

        Page<CardSet> page = new PageImpl<>(sets);

        PaginationRequest pagination = PaginationRequest.builder().page(0).size(10).build();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize());

        // when
        when(setRepository.findByNameContainingIgnoreCaseAndType(name, SetType.PUBLIC, pageRequest)).thenReturn(page);
        PaginationResponse<CardSetDto> response = setService.getPublicSetsByName(name, pagination);

        // then
        verify(setRepository).findByNameContainingIgnoreCaseAndType(name, SetType.PUBLIC, pageRequest);
        assertThat(response.getContent(), hasSize(sets.size()));
        assertThat(response.getPage(), is(pagination.getPage()));
        assertThat(response.getSize(), is(sets.size()));
    }

    @Test
    void givenGetSetsByAuthorAndName_whenUserIsTheAuthor_thenReturnAllSetsOfTheUserWithGivenName() {
        // given
        long userId = 2;
        String name = "ren";

        String email = "j.doe@mail.com";
        User user = User.builder().id(userId).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        List<CardSet> sets = List.of(
                CardSet.builder().id(1L).author(user).name("French").type(SetType.PRIVATE).build(),
                CardSet.builder().id(2L).author(user).name("French B1 vocab").type(SetType.PUBLIC).build()
        );

        Page<CardSet> page = new PageImpl<>(sets);

        PaginationRequest pagination = PaginationRequest.builder().page(0).size(10).build();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize());

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findByAuthorAndNameContainingIgnoreCase(user, name, pageRequest)).thenReturn(page);

        PaginationResponse<CardSetDto> response = setService.getSetsByAuthorAndName(userId, name, pagination, auth);

        // then
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(email);
        verify(setRepository).findByAuthorAndNameContainingIgnoreCase(user, name, pageRequest);

        assertThat(response.getContent(), hasSize(sets.size()));
        assertThat(response.getPage(), is(pagination.getPage()));
        assertThat(response.getSize(), is(sets.size()));
    }

    @Test
    void givenGetSetsByAuthorAndName_whenUserIsNotTheAuthor_thenReturnPublicSetsOfTheUserWithGivenName() {
        // given
        long userId = 2;
        String name = "ren";

        User other = User.builder().id(userId).email("test@mail.com").build();

        String email = "j.doe@mail.com";
        User user = User.builder().id(3).email(email).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        List<CardSet> sets = List.of(
                CardSet.builder().id(2L).author(other).name("French").type(SetType.PUBLIC).build()
        );
        Page<CardSet> page = new PageImpl<>(sets);

        PaginationRequest pagination = PaginationRequest.builder().page(0).size(10).build();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize());

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(other));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(setRepository.findByAuthorAndNameContainingIgnoreCaseAndType(other, name, SetType.PUBLIC, pageRequest)).thenReturn(page);

        PaginationResponse<CardSetDto> response = setService.getSetsByAuthorAndName(userId, name, pagination, auth);

        // then
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(email);
        verify(setRepository).findByAuthorAndNameContainingIgnoreCaseAndType(other, name, SetType.PUBLIC, pageRequest);

        assertThat(response.getContent(), hasSize(sets.size()));
        assertThat(response.getPage(), is(pagination.getPage()));
        assertThat(response.getSize(), is(sets.size()));
    }

    @Test
    void givenGetPublicSets_thenReturnAllPublicSets() {
        // given
        List<CardSet> sets = List.of(
                CardSet.builder().id(2L).name("French").type(SetType.PUBLIC).build(),
                CardSet.builder().id(2L).name("Spanish").type(SetType.PUBLIC).build()
        );
        Page<CardSet> page = new PageImpl<>(sets);

        PaginationRequest pagination = PaginationRequest.builder().page(0).size(10).build();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize());

        // when
        when(setRepository.findByType(SetType.PUBLIC, pageRequest)).thenReturn(page);
        PaginationResponse<CardSetDto> response = setService.getPublicSets(pagination);

        // then
        verify(setRepository).findByType(SetType.PUBLIC, pageRequest);

        assertThat(response.getContent(), hasSize(sets.size()));
        assertThat(response.getPage(), is(pagination.getPage()));
        assertThat(response.getSize(), is(sets.size()));
    }
}