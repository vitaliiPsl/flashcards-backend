package com.example.flashcards.repository;

import com.example.flashcards.model.CardSet;
import com.example.flashcards.model.SetType;
import com.example.flashcards.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardSetRepository extends JpaRepository<CardSet, Long> {
    Optional<CardSet> findByNameAndAuthor(String name, User author);

    Page<CardSet> findByAuthor(User author, Pageable pageable);

    Page<CardSet> findByAuthorAndType(User author, SetType type, Pageable pageable);

    Page<CardSet> findByNameContainingIgnoreCaseAndType(String name, SetType type, Pageable pageable);

    Page<CardSet> findByAuthorAndNameContainingIgnoreCase(User author, String name, Pageable pageable);

    Page<CardSet> findByAuthorAndNameContainingIgnoreCaseAndType(User author, String name, SetType type, Pageable pageable);

    Page<CardSet> findByType(SetType type, Pageable pageable);
}
