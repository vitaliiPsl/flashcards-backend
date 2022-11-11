package com.example.flashcards.service.impl;

import com.example.flashcards.dto.CardDto;
import com.example.flashcards.dto.CardSetDto;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.Card;
import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.SetType;
import com.example.flashcards.model.User;
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
    public CardSetDto saveSet(CardSetDto cardSetDto, Authentication authentication) {
        User user = getUser(authentication);
        String setName = cardSetDto.getName();

        Optional<CardSet> existing = cardSetRepository.findByNameAndAuthor(setName, user);
        if (existing.isPresent()) {
            throw new ResourceAlreadyExist(setName, CardSet.class);
        }

        CardSet set = mappers.mapCardSetDtoToCardSet(cardSetDto);
        set.setAuthor(user);
        set.setCreatedAt(LocalDateTime.now());

        Set<Card> cards = mapCards(set, cardSetDto.getCards());
        set.setCards(cards);

        CardSet saved = cardSetRepository.save(set);
        return mappers.mapCardSetToCardSetDto(saved);
    }

    @Override
    public void deleteSet(long id, Authentication authentication) {
        CardSet set = getSetAndVerifyAuthor(id, authentication);

        cardSetRepository.delete(set);
    }

    @Override
    public CardSetDto replaceSet(long id, CardSetDto cardSetDto, Authentication authentication) {
        CardSet existingSet = getSetAndVerifyAuthor(id, authentication);

        CardSet set = mappers.mapCardSetDtoToCardSet(cardSetDto);
        set.setId(id);
        set.setAuthor(existingSet.getAuthor());
        set.setCreatedAt(existingSet.getCreatedAt());
        set.setUpdatedAt(LocalDateTime.now());

        Set<Card> cards = mapCards(set, cardSetDto.getCards());
        set.setCards(cards);

        cardSetRepository.save(set);
        return mappers.mapCardSetToCardSetDto(set);
    }

    @Override
    public CardSetDto getSetById(long id, Authentication authentication) {

        Optional<CardSet> optionalSet = cardSetRepository.findById(id);
        if (optionalSet.isEmpty()) {
            throw new ResourceNotFound(id, CardSet.class);
        }

        User user = getUser(authentication);
        CardSet set = optionalSet.get();
        if (!set.getAuthor().equals(user) && set.getType() == SetType.PRIVATE) {
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
        card.setCreatedAt(LocalDateTime.now());

        return card;
    }

    private CardSet getSetAndVerifyAuthor(long id, Authentication authentication) {
        User user = getUser(authentication);

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

    private User getUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound(email, User.class));
    }
}
