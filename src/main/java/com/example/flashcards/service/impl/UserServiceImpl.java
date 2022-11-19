package com.example.flashcards.service.impl;

import com.example.flashcards.dto.user.UserDto;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.UserService;
import com.example.flashcards.service.utils.DtoMappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DtoMappers mappers;

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long id) {
        log.info("Get user by id: {}", id);

        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()) {
            log.error("User with id {} doesn't exist", id);
            throw new ResourceNotFound(id, User.class);
        }

        return mappers.mapUserToUserDto(optionalUser.get());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getAuthenticatedUser(Authentication auth) {
        log.info("Get authenticated user: {}", auth);

        if(auth == null) {
            log.error("User is not authenticated");
            throw new IllegalArgumentException("User is not authenticated");
        }

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFound(auth.getName(), User.class));

        return mappers.mapUserToUserDto(user);
    }
}
