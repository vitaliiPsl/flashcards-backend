package com.example.flashcards.service.impl;

import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.utils.DtoMappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    UserRepository userRepository;
    DtoMappers mappers;

    UserServiceImpl userService;

    @BeforeEach
    void init() {
        userRepository = Mockito.mock(UserRepository.class);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mappers = Mockito.spy(new DtoMappers(modelMapper));

        userService = new UserServiceImpl(userRepository, mappers);
    }

    @Test
    void givenGetUserById_whenUserWithGivenIdExist_thenReturnThatUser() {
        // given
        long id = 2;
        User user = User.builder().id(id).email("j.doe@mail.com").build();

        // when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(id);

        // then
        verify(userRepository).findById(id);
        verify(mappers).mapUserToUserDto(user);

        assertThat(result.getId(), is(id));
        assertThat(result.getEmail(), is(user.getEmail()));
    }

    @Test
    void givenGetUserById_whenUserWithGivenIdDoesntExist_thenThrowException() {
        // given
        long id = 2;

        // when
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFound.class, () -> userService.getUserById(id));
    }

    @Test
    void givenGetAuthenticatedUser_whenUserIsAuthenticated_thenReturnThatUser() {
        // given
        String email = "j.doe@mail.com";
        User user = User.builder().id(2).email(email).build();

        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDto result = userService.getAuthenticatedUser(auth);

        // then
        verify(userRepository).findByEmail(email);
        verify(mappers).mapUserToUserDto(user);

        assertThat(result.getId(), is(user.getId()));
        assertThat(result.getEmail(), is(user.getEmail()));
    }

    @Test
    void givenGetAuthenticatedUser_whenUserIsNotAuthenticated_thenThrowException() {
        // given
        Authentication auth = null;

        // then
        assertThrows(IllegalArgumentException.class, () -> userService.getAuthenticatedUser(auth));
    }

    @Test
    void givenGetAuthenticatedUser_whenUserIsAuthenticatedButWasntFoundInDb_thenThrowException() {
        // given
        String email = "j.doe@mail.com";

        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFound.class, () -> userService.getAuthenticatedUser(auth));
    }
}