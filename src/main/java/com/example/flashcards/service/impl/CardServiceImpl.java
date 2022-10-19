package com.example.flashcards.service.impl;

import com.example.flashcards.dto.CardDto;
import com.example.flashcards.dto.requests.PaginationRequest;
import com.example.flashcards.dto.responses.PaginationResponse;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.Card;
import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.CardRepository;
import com.example.flashcards.repository.CardSetRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    private final ModelMapper modelMapper;

    @Override
    public CardDto saveCard(long setId, CardDto cardDto, Authentication auth) {
        CardSet set = getSetAndVerifyAuthor(setId, auth);

        Card card = mapCardDtoToCard(cardDto);
        card.setSet(set);
        card.setCreatedAt(LocalDateTime.now());

        if(set.getCards().contains(card)) {
            throw new ResourceAlreadyExist(card.getFront(), Card.class);
        }

        Card saved = cardRepository.save(card);
        return mapCardToCardDto(saved);
    }

    @Override
    public void deleteCard(long cardId, long setId, Authentication auth) {
        Card card = getCardVerifySetAndAuthor(cardId, setId, auth);

        cardRepository.delete(card);
    }

    @Override
    public CardDto replaceCard(long cardId, long setId, CardDto cardDto, Authentication auth) {
        Card existing = getCardVerifySetAndAuthor(cardId, setId, auth);

        Card card = mapCardDtoToCard(cardDto);
        card.setId(cardId);
        card.setSet(existing.getSet());
        card.setCreatedAt(existing.getCreatedAt());
        card.setUpdatedAt(LocalDateTime.now());

        Card saved = cardRepository.save(card);
        return mapCardToCardDto(saved);
    }

    @Override
    public CardDto getCardById(long cardId, long setId, Authentication auth) {
        CardSet set = getSet(setId);
        Card card = getCard(cardId);

        if(!belongsToSet(set, card)) {
            throw new ResourceNotAccessible(cardId, Card.class);
        }

        User user = getUser(auth);
        if(!isSetAuthor(user, set) && set.isPrivate()) {
            throw new ResourceNotAccessible(setId, CardSet.class);
        }

        return mapCardToCardDto(card);
    }

    @Override
    public PaginationResponse<CardDto> getCards(long setId, Authentication auth, PaginationRequest pagination) {
        CardSet set = getSet(setId);

        User user = getUser(auth);
        if(!isSetAuthor(user, set) && set.isPrivate()) {
            throw new ResourceNotAccessible(setId, CardSet.class);
        }

        PageRequest page = PageRequest.of(pagination.getPage(), pagination.getSize(), Sort.by("createdAt"));
        Page<CardDto> cardDtoPage = cardRepository.findBySet(set, page).map(this::mapCardToCardDto);

        return new PaginationResponse<>(cardDtoPage, pagination);
    }

    private Card getCardVerifySetAndAuthor(long cardId, long setId, Authentication auth) {
        CardSet set = getSetAndVerifyAuthor(setId, auth);
        Card card = getCard(cardId);

        if(!belongsToSet(set, card)) {
            throw new ResourceNotAccessible(cardId, Card.class);
        }

        return card;
    }

    private CardSet getSetAndVerifyAuthor(long setId, Authentication auth) {
        User user = getUser(auth);
        CardSet set = getSet(setId);

        if(!isSetAuthor(user, set)) {
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

    private Card mapCardDtoToCard(CardDto cardDto) {
        return modelMapper.map(cardDto, Card.class);
    }

    private CardDto mapCardToCardDto(Card card) {
        return modelMapper.map(card, CardDto.class);
    }
}