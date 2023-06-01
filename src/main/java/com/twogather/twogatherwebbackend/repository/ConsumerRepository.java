package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    boolean existsByEmail(String email);
    Optional<Consumer> findByEmail(String email);

    @Query("select c from Consumer c where c.memberId = :consumerId and c.isActive = true")
    Optional<Consumer> findActiveConsumerById(@Param("consumerId") Long consumerId);
}
