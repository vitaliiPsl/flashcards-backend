package com.example.flashcards.service.impl;

import com.example.flashcards.dto.CardSetDto;
import com.example.flashcards.exceptions.ResourceAlreadyExist;
import com.example.flashcards.exceptions.ResourceNotAccessible;
import com.example.flashcards.exceptions.ResourceNotFound;
import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.SetType;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.CardSetRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.service.CardSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CardSetServiceImpl implements CardSetService {
    private final CardSetRepository cardSetRepository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    public CardSetDto saveSet(CardSetDto cardSetDto, Authentication authentication) {
        User user = getUser(authentication);
        String setName = cardSetDto.getName();

        Optional<CardSet> existing = cardSetRepository.findByNameAndAuthor(setName, user);
        if (existing.isPresent()) {
            throw new ResourceAlreadyExist(setName, CardSet.class);
        }

        CardSet set = mapCardSetDtoToCardSet(cardSetDto);
        set.setAuthor(user);
        set.getCards().forEach(card -> card.setSet(set));

        CardSet saved = cardSetRepository.save(set);
        return mapCardSetToCardSetDto(saved);
    }

    @Override
    public void deleteSet(long id, Authentication authentication) {
        CardSet set = getSetAndVerifyAuthor(id, authentication);

        cardSetRepository.delete(set);
    }

    @Override
    public CardSetDto replaceSet(long id, CardSetDto cardSetDto, Authentication authentication) {
        CardSet existingSet = getSetAndVerifyAuthor(id, authentication);

        CardSet set = mapCardSetDtoToCardSet(cardSetDto);
        set.setId(id);
        set.setAuthor(existingSet.getAuthor());

        cardSetRepository.save(set);

        return mapCardSetToCardSetDto(set);
    }

    @Override
    public CardSetDto getSetById(long id, Authentication authentication) {

        Optional<CardSet> optionalSet = cardSetRepository.findById(id);
        if(optionalSet.isEmpty()) {
            throw new ResourceNotFound(id, CardSet.class);
        }

        User user = getUser(authentication);
        CardSet set = optionalSet.get();
        if(!set.getAuthor().equals(user) && set.getType() == SetType.PRIVATE) {
            throw new ResourceNotAccessible(id, user, CardSet.class);
        }

        return mapCardSetToCardSetDto(set);
    }

    private CardSet getSetAndVerifyAuthor(long id, Authentication authentication) {
        User user = getUser(authentication);

        Optional<CardSet> optionalCardSet = cardSetRepository.findById(id);
        if(optionalCardSet.isEmpty()) {
            throw new ResourceNotFound(id, CardSet.class);
        }

        CardSet cardSet = optionalCardSet.get();
        if(!cardSet.getAuthor().equals(user)) {
            throw new ResourceNotAccessible(id, user, CardSet.class);
        }

        return cardSet;
    }

    private User getUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound(email, User.class));
    }

    private CardSet mapCardSetDtoToCardSet(CardSetDto cardSetDto) {
        return modelMapper.map(cardSetDto, CardSet.class);
    }

    private CardSetDto mapCardSetToCardSetDto(CardSet cardSet) {
        return modelMapper.map(cardSet, CardSetDto.class);
    }
}
