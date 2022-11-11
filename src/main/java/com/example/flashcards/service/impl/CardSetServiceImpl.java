package com.example.flashcards.service.impl;

import com.example.flashcards.dto.CardDto;
import com.example.flashcards.dto.CardSetDto;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.*;
import com.example.flashcards.model.learning.Difficulty;
import com.example.flashcards.repository.CardSetRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.CardSetService;
import com.example.flashcards.service.utils.DtoMappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CardSetServiceImpl implements CardSetService {
    private final CardSetRepository cardSetRepository;
    private final UserRepository userRepository;

    private final DtoMappers mappers;

    @Override
    public CardSetDto saveSet(CardSetDto cardSetDto, Authentication auth) {
        log.info("Create set: {}", cardSetDto);

        User user = getUser(auth);
        String setName = cardSetDto.getName();

        Optional<CardSet> existing = cardSetRepository.findByNameAndAuthor(setName, user);
        if (existing.isPresent()) {
            log.error("User {} already has set with name '{}'", user, setName);
            throw new ResourceAlreadyExist(setName, CardSet.class);
        }

        CardSet set = mappers.mapCardSetDtoToCardSet(cardSetDto);
        set.setAuthor(user);
        set.setCreatedAt(LocalDateTime.now());

        Set<Card> cards = mapCards(set, cardSetDto.getCards());
        set.setCards(cards);

        set = cardSetRepository.save(set);
        return mappers.mapCardSetToCardSetDto(set);
    }

    @Override
    public void deleteSet(long id, Authentication auth) {
        log.info("Delete set with id {}", id);

        CardSet set = getSetAndVerifyAuthor(id, auth);

        cardSetRepository.delete(set);
    }

    @Override
    public CardSetDto replaceSet(long id, CardSetDto cardSetDto, Authentication auth) {
        log.info("Replace set with id {}", id);

        CardSet existing = getSetAndVerifyAuthor(id, auth);
        existing.setName(cardSetDto.getName());
        existing.setDescription(cardSetDto.getDescription());
        existing.setType(cardSetDto.getType());
        existing.setUpdatedAt(LocalDateTime.now());

        return mappers.mapCardSetToCardSetDto(existing);
    }

    @Override
    public CardSetDto getSetById(long id, Authentication auth) {
        log.info("Get set with id {}", id);

        Optional<CardSet> optionalSet = cardSetRepository.findById(id);
        if (optionalSet.isEmpty()) {
            log.error("Set with id {} doesn't exist", id);
            throw new ResourceNotFound(id, CardSet.class);
        }
        CardSet set = optionalSet.get();

        User user = getUser(auth);
        if (!set.getAuthor().equals(user) && set.getType() == SetType.PRIVATE) {
            log.error("User with id {} has no access to set {}", user.getId(), id);
            throw new ResourceNotAccessible(id, user, CardSet.class);
        }

        return mappers.mapCardSetToCardSetDto(set);
    }

    @Override
    public List<CardSetDto> getSetsByAuthor(long authorId, Authentication auth) {
        log.info("Get sets made by author with id {}", authorId);

        User author = getUser(authorId);
        User authenticatedUser = getUser(auth);

        List<CardSet> sets;

        if (!author.equals(authenticatedUser)) {
            log.info("Get public sets of author with id {}", authorId);
            sets = cardSetRepository.findByAuthorAndType(author, SetType.PUBLIC);
        } else {
            log.info("Get all sets of author with id {}", authorId);
            sets = cardSetRepository.findByAuthor(author);
        }

        return sets.stream().map(mappers::mapCardSetToCardSetDto).collect(Collectors.toList());
    }

    private Set<Card> mapCards(CardSet cardSet, Set<CardDto> cardsDto) {
        return cardsDto.stream().map(cardDto -> mapCard(cardSet, cardDto)).collect(Collectors.toSet());
    }

    private Card mapCard(CardSet cardSet, CardDto cardDto) {
        Card card = mappers.mapCardDtoToCard(cardDto);
        card.setSet(cardSet);
        card.setDifficulty(Difficulty.HARD);
        card.setCreatedAt(LocalDateTime.now());

        return card;
    }

    private CardSet getSetAndVerifyAuthor(long id, Authentication auth) {
        User user = getUser(auth);

        Optional<CardSet> optionalCardSet = cardSetRepository.findById(id);
        if (optionalCardSet.isEmpty()) {
            throw new ResourceNotFound(id, CardSet.class);
        }

        CardSet cardSet = optionalCardSet.get();
        if (!cardSet.getAuthor().equals(user)) {
            throw new ResourceNotAccessible(id, user, CardSet.class);
        }

        return cardSet;
    }

    private User getUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFound(id, User.class));
    }

    private User getUser(Authentication auth) {
        String email = auth.getName();

        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound(email, User.class));
    }
}
