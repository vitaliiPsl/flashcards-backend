package com.example.flashcards.repository;

import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.SetType;
import com.example.flashcards.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardSetRepository extends JpaRepository<CardSet, Long> {
    Optional<CardSet> findByName(String name);

    Optional<CardSet> findByNameAndAuthor(String name, User author);

    List<CardSet> findByAuthor(User author);

    List<CardSet> findByAuthorAndType(User author, SetType type);
}
