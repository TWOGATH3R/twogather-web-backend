package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreOwnerRepository extends JpaRepository<StoreOwner, Long> {
    boolean existsByUsername(String username);
    Optional<StoreOwner> findByUsername(String username);
    @Query("select m from StoreOwner m where m.memberId =:memberId and m.isActive = true")
    Optional<Member> findActiveMemberById(@Param("memberId") Long memberId);
}
