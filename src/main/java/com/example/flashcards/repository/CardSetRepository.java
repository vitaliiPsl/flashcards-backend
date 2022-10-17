package com.example.flashcards.repository;

import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardSetRepository extends JpaRepository<CardSet, Long> {
    Optional<CardSet> findByName(String name);
    Optional<CardSet> findByNameAndAuthor(String name, User author);
}
