package com.example.flashcards.service.impl;

import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.dto.auth.AuthRequest;
import com.example.flashcards.dto.auth.AuthResponse;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.AuthService;
import com.example.flashcards.service.JwtService;
import com.example.flashcards.service.utils.DtoMappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final DtoMappers mappers;

    @Override
    public AuthResponse signIn(AuthRequest authRequest) {
        log.debug("Authenticate user: {}", authRequest);

        Authentication authentication = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        authentication = authManager.authenticate(authentication);

        String jwtToken = jwtService.createToken(authentication);
        return new AuthResponse(jwtToken);
    }

    @Override
    public UserDto signUp(UserDto userDto) {
        log.debug("Register new user: {}", userDto);

        Optional<User> possibleUser = userRepository.findByEmail(userDto.getEmail());
        if (possibleUser.isPresent()) {
            log.warn("User with email: '{}' already exists", userDto.getEmail());
            throw new ResourceAlreadyExist(userDto.getEmail(), User.class);
        }

        possibleUser = userRepository.findByNickname(userDto.getNickname());
        if (possibleUser.isPresent()) {
            log.warn("User with username: '{}' already exists", userDto.getNickname());
            throw new ResourceAlreadyExist(userDto.getNickname(), User.class);
        }

        User user = saveNewUser(userDto);
        return mappers.mapUserToUserDto(user);
    }

    private User saveNewUser(UserDto userDto) {
        User user = mappers.mapUserDtoToUser(userDto);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setCreateAt(LocalDateTime.now());

        if(user.getNickname() == null) {
            user.setNickname(user.getEmail());
        }

        user = userRepository.save(user);
        return user;
    }
}
