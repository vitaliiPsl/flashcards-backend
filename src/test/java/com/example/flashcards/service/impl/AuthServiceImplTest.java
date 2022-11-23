package com.example.flashcards.service.impl;

import com.example.flashcards.dto.auth.AuthRequest;
import com.example.flashcards.dto.auth.AuthResponse;
import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.JwtService;
import com.example.flashcards.service.utils.DtoMappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authManager;

    @Mock
    JwtService jwtService;

    DtoMappers mappers;

    AuthServiceImpl authService;

    @BeforeEach
    void init() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mappers = Mockito.spy(new DtoMappers(modelMapper));

        authService = new AuthServiceImpl(userRepository, passwordEncoder, authManager, jwtService, mappers);
    }

    @Test
    void givenSignIn_whenCredentialsAreValid_thenBuildJwtToken() {
        // given
        String email = "j.doe@mail.com";
        String password = "password";
        AuthRequest request = AuthRequest.builder().email(email).password(password).build();

        Authentication auth = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        String jwt = "eyJhbGciOiJIU.zI1NiIsInR5c.CI6IkpXVCJ9";

        // when
        when(authManager.authenticate(auth)).thenReturn(auth);
        when(jwtService.createToken(auth)).thenReturn(jwt);

        AuthResponse response = authService.signIn(request);

        // then
        verify(authManager).authenticate(auth);
        verify(jwtService).createToken(auth);

        assertThat(response.getJwt(), is(jwt));
    }

    @Test
    void givenSignIn_whenCredentialsAreInvalid_thenThrowException() {
        // given
        String email = "j.doe@mail.com";
        String password = "password";
        AuthRequest request = AuthRequest.builder().email(email).password(password).build();

        Authentication auth = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        // when
        when(authManager.authenticate(auth)).thenThrow(new BadCredentialsException("Bad credentials"));

        // then
        assertThrows(AuthenticationException.class, () -> authService.signIn(request));
        verify(authManager).authenticate(auth);
    }

    @Test
    void givenSignUp_whenUserDtoIsInvalid_thenSaveUser() {
        // given
        String email = "mail@mail.com";
        String nickname = "test.nickname";
        String password = "password";
        String encodedPassword = "%1gea#b12";

        UserDto userDto = UserDto.builder().email(email).nickname(nickname)
                .password(password).build();

        User user = User.builder().email(email).nickname(nickname)
                .password(password).build();

        // when
        when(mappers.mapUserDtoToUser(userDto)).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        UserDto result = authService.signUp(userDto);

        // then
        verify(mappers).mapUserDtoToUser(userDto);
        verify(userRepository).findByEmail(email);
        verify(userRepository).findByNickname(nickname);
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(password);
        verify(mappers).mapUserToUserDto(user);

        assertThat(user.getPassword(), is(encodedPassword));
        assertThat(user.getCreateAt(), notNullValue());
        assertThat(user.isEnabled(), is(true));
        assertThat(result.getEmail(), is(userDto.getEmail()));
        assertThat(result.getNickname(), is(userDto.getNickname()));
    }

    @Test
    void givenSignUp_whenNicknameNotProvided_thenSetNicknameEqualToEmail() {
        // given
        String email = "mail@mail.com";
        String password = "password";
        String encodedPassword = "%1gea#b12";

        UserDto userDto = UserDto.builder().email(email).password(password).build();

        User user = User.builder().email(email).password(password).build();

        // when
        when(mappers.mapUserDtoToUser(userDto)).thenReturn(user);
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByNickname(userDto.getNickname())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        UserDto result = authService.signUp(userDto);

        // then
        verify(mappers).mapUserDtoToUser(userDto);
        verify(userRepository).findByEmail(userDto.getEmail());
        verify(userRepository).findByNickname(userDto.getNickname());
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(password);
        verify(mappers).mapUserToUserDto(user);

        assertThat(user.getPassword(), is(encodedPassword));
        assertThat(user.getCreateAt(), notNullValue());
        assertThat(user.isEnabled(), is(true));
        assertThat(result.getEmail(), is(userDto.getEmail()));
        assertThat(result.getNickname(), is(userDto.getEmail()));
    }

    @Test
    void givenSignUp_whenUserWithProvidedEmailAlreadyExist_thenThrowException() {
        // given
        String email = "mail@mail.com";
        UserDto userDto = UserDto.builder().email(email).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().email(email).build()));

        // then
        assertThrows(ResourceAlreadyExist.class, () -> authService.signUp(userDto));
    }

    @Test
    void givenSignUp_whenUserWithProvidedNicknameAlreadyExist_thenThrowException() {
        // given
        String email = "mail@mail.com";
        String nickname = "john.doe";
        UserDto userDto = UserDto.builder().email(email).nickname(nickname).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByNickname(nickname)).thenReturn(Optional.of(User.builder().nickname(nickname).build()));

        // then
        assertThrows(ResourceAlreadyExist.class, () -> authService.signUp(userDto));
    }
}