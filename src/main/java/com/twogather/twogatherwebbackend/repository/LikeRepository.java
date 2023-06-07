package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    int deleteByStoreStoreIdAndMemberMemberId(Long storeId, Long memberId);
    @Query("SELECT l FROM Likes l WHERE l.store.storeId = :storeId AND l.member.memberId = :memberId")
    Optional<Likes> findByStoreIdAndMemberId(@Param("storeId") Long storeId, @Param("memberId") Long memberId);
}
