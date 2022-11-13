package com.example.flashcards.service;

import com.example.flashcards.dto.pagination.PaginationRequest;
import com.example.flashcards.dto.pagination.PaginationResponse;
import com.example.flashcards.dto.set.CardSetDto;
import org.springframework.security.core.Authentication;

/**
 * Business logic for the card sets
 */
public interface CardSetService {

    /**
     * Saves a new card set for the currently authenticated user
     *
     * @param cardSetDto     new set
     * @param authentication authentication for this request
     * @return saved set
     */
    CardSetDto saveSet(CardSetDto cardSetDto, Authentication authentication);

    /**
     * Deletes the set with given id if current user is the set owner
     *
     * @param id             id of the user
     * @param authentication authentication for this request
     */
    void deleteSet(long id, Authentication authentication);

    /**
     * Replaces the existing card set with the new card set data
     *
     * @param id             id of the existing set
     * @param cardSetDto     new set
     * @param authentication authentication for this request
     * @return updated set
     */
    CardSetDto replaceSet(long id, CardSetDto cardSetDto, Authentication authentication);

    /**
     * Retrieve the set by id if it exists and if it is public or the user is the author of the set
     *
     * @param id             id of the set
     * @param authentication authentication for this request
     * @return card set or throw an exception if the card set with provided id doesn't exist
     * or the card set is not accessible for the current user
     */
    CardSetDto getSetById(long id, Authentication authentication);

    /**
     * Retrieve sets created by author with provided id
     *
     * @param authorId id of the author
     * @param auth     logged in user
     * @return retrieved sets
     */
    PaginationResponse<CardSetDto> getSetsByAuthor(long authorId, PaginationRequest pagination, Authentication auth);

    /**
     * Retrieve sets with given name
     *
     * @param name       name of the sets
     * @param pagination pagination request
     * @return retrieved sets
     */
    PaginationResponse<CardSetDto> getPublicSetsByName(String name, PaginationRequest pagination);

    /**
     * Retrieve sets by author and name
     * @param authorId id of the author
     * @param name name of the sets
     * @param pagination pagination request
     * @param auth authentication
     * @return retrieved sets
     */
    PaginationResponse<CardSetDto> getSetsByAuthorAndName(long authorId, String name, PaginationRequest pagination, Authentication auth);

    /**
     * Retrieve public sets
     *
     * @param pagination pagination request
     * @return retrieved sets
     */
    PaginationResponse<CardSetDto> getPublicSets(PaginationRequest pagination);
}
