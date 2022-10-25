package com.example.flashcards.service.utils;

import com.example.flashcards.dto.CardDto;
import com.example.flashcards.dto.CardSetDto;
import com.example.flashcards.dto.UserDto;
import com.example.flashcards.model.Card;
import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DtoMappers {
    private final ModelMapper modelMapper;

    public User mapUserDtoToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto mapUserToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public CardSet mapCardSetDtoToCardSet(CardSetDto cardSetDto) {
        return modelMapper.map(cardSetDto, CardSet.class);
    }

    public CardSetDto mapCardSetToCardSetDto(CardSet cardSet) {
        return modelMapper.map(cardSet, CardSetDto.class);
    }

    public Card mapCardDtoToCard(CardDto cardDto) {
        return modelMapper.map(cardDto, Card.class);
    }

    public CardDto mapCardToCardDto(Card card) {
        return modelMapper.map(card, CardDto.class);
    }
}