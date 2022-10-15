package com.example.flashcards.service.impl;

import com.example.flashcards.dto.UserDto;
import com.example.flashcards.dto.requests.AuthRequest;
import com.example.flashcards.dto.responses.AuthResponse;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.AuthService;
import com.example.flashcards.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ModelMapper modelMapper;

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

        User user = mapUserDtoToUser(userDto);
        System.out.println(user);

        Optional<User> possibleUser = userRepository.findByEmail(user.getEmail());
        if(possibleUser.isPresent()) {
            log.warn("User with email: '{}' already exists", user.getEmail());
            throw new ResourceAlreadyExist(user.getEmail(), User.class);
        }

        possibleUser = userRepository.findByUsername(user.getUsername());
        if(possibleUser.isPresent()) {
            log.warn("User with username: '{}' already exists", user.getUsername());
            throw new ResourceAlreadyExist(user.getUsername(), User.class);
        }

        user = saveNewUser(user);

        return mapUserToUserDto(user);
    }

    private User saveNewUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);

        user = userRepository.save(user);
        return user;
    }

    private User mapUserDtoToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private UserDto mapUserToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
