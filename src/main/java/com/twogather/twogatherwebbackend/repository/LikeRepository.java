package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    @Modifying
    @Query("delete from Likes l where l.store.storeId = :storeId and l.member.memberId = :memberId")
    int deleteByStoreIdAndMemberId(@Param("storeId")  Long storeId, @Param("memberId") Long memberId);
    @Query("SELECT l FROM Likes l WHERE l.store.storeId = :storeId AND l.member.memberId = :memberId")
    Optional<Likes> findByStoreIdAndMemberId(@Param("storeId") Long storeId, @Param("memberId") Long memberId);
}
