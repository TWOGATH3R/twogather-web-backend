package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    int deleteByStoreStoreIdAndMemberMemberId(Long storeId, Long memberId);
    List<Likes> findByStoreStoreIdAndMemberMemberId(Long storeId, Long memberId);
}
