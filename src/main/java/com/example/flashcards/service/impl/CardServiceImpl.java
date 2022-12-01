package com.example.flashcards.service.impl;

import com.example.flashcards.dto.card.CardDto;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.Card;
import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.User;
import com.example.flashcards.model.learning.Difficulty;
import com.example.flashcards.repository.CardRepository;
import com.example.flashcards.repository.CardSetRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.CardService;
import com.example.flashcards.service.utils.DtoMappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardSetRepository setRepository;
    private final UserRepository userRepository;

    private final DtoMappers mappers;

    @Override
    public CardDto saveCard(long setId, CardDto cardDto, Authentication auth) {
        log.info("Add card {} to the set with id {}", cardDto, setId);
        CardSet set = getSetAndVerifyAuthor(setId, auth);

        Card card = mappers.mapCardDtoToCard(cardDto);
        card.setSet(set);
        card.setDifficulty(Difficulty.HARD);
        card.setCreatedAt(LocalDateTime.now());

        if (set.getCards().contains(card)) {
            log.error("Card with such properties {} is already present in set with id {}", cardDto, setId);
            throw new ResourceAlreadyExist(card.getFront(), Card.class);
        }

        Card saved = cardRepository.save(card);
        return mappers.mapCardToCardDto(saved);
    }

    @Override
    public void deleteCard(long cardId, long setId, Authentication auth) {
        log.info("Delete card with id {} from set {}", cardId, setId);
        Card card = getCardVerifySetAndAuthor(cardId, setId, auth);

        cardRepository.delete(card);
    }

    @Override
    public CardDto replaceCard(long cardId, long setId, CardDto cardDto, Authentication auth) {
        log.info("Replace card with identifier {} from set with id {}. Replacement: {}", cardId, setId, cardDto);

        Card existing = getCardVerifySetAndAuthor(cardId, setId, auth);
        existing.setFront(cardDto.getFront());
        existing.setBack(cardDto.getBack());
        existing.setUpdatedAt(LocalDateTime.now());

        if (isCardFrontValueTaken(existing.getSet(), existing)) {
            log.error("There already is in this set a card with given front value: {}", existing.getFront());
            throw new ResourceAlreadyExist(existing.getFront(), Card.class);
        }

        Card saved = cardRepository.save(existing);
        return mappers.mapCardToCardDto(saved);
    }

    @Override
    public CardDto getCardById(long cardId, long setId, Authentication auth) {
        log.info("Get card with id {} that belongs to set {}", cardId, setId);
        CardSet set = getSet(setId);
        Card card = getCard(cardId);

        if (!belongsToSet(set, card)) {
            log.error("Card with id {} doesn't belong to set {}", cardId, setId);
            throw new ResourceNotAccessible(cardId, Card.class);
        }

        User user = getUser(auth);
        if (!isSetAuthor(user, set) && set.isPrivate()) {
            log.error("Card with id {} is private", card);
            throw new ResourceNotAccessible(setId, CardSet.class);
        }

        return mappers.mapCardToCardDto(card);
    }

    private Card getCardVerifySetAndAuthor(long cardId, long setId, Authentication auth) {
        CardSet set = getSetAndVerifyAuthor(setId, auth);
        Card card = getCard(cardId);

        if (!belongsToSet(set, card)) {
            log.error("Card with id {} doesn't belong to set {}", card, setId);
            throw new ResourceNotAccessible(cardId, Card.class);
        }

        return card;
    }

    private CardSet getSetAndVerifyAuthor(long setId, Authentication auth) {
        User user = getUser(auth);
        CardSet set = getSet(setId);

        if (!isSetAuthor(user, set)) {
            log.error("User with identifier {} has no access to the set with id {}", user.getId(), setId);
            throw new ResourceNotAccessible(setId, user, CardSet.class);
        }

        return set;
    }

    private Card getCard(long cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> new ResourceNotFound(cardId, Card.class));
    }

    private User getUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound(email, User.class));
    }

    private CardSet getSet(long setId) {
        return setRepository.findById(setId).orElseThrow(() -> new ResourceNotFound(setId, Set.class));
    }

    private static boolean isSetAuthor(User user, CardSet set) {
        return set.getAuthor().equals(user);
    }

    private static boolean belongsToSet(CardSet set, Card card) {
        return card.getSet().equals(set);
    }

    private static boolean isCardFrontValueTaken(CardSet set, Card card) {
        return set.getCards().stream().anyMatch(
                other -> {
                    boolean frontSidesEqual = other.getFront().equals(card.getFront());
                    boolean idsEqual = other.getId().equals(card.getId());
                    return frontSidesEqual && !idsEqual;
                }
        );
    }
}
