package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    int deleteByStoreStoreIdAndMemberMemberId(Long storeId, Long memberId);
    Optional<Likes> findByStoreStoreIdAndMemberMemberId(Long storeId, Long memberId);
}
