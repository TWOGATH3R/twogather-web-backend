package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 특정 가게의 리뷰 목록 조회(Paging 적용)
    Page<Review> findByStoreStoreId(Long storeId, Pageable pageable);
}
