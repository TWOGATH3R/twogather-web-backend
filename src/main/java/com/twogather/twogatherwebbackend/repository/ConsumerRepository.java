package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    @Query("select m from Consumer m where m.memberId =:memberId and m.isActive = true")
    Optional<Consumer> findActiveMemberById(@Param("memberId") Long memberId);
}
