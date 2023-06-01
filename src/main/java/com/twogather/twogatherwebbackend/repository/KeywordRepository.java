package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByName(String name);
    @Query(value = "SELECT * FROM keyword ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Keyword> findRandomKeywords(int count);
}
