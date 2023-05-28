package com.twogather.twogatherwebbackend.repository.review;

import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

// QueryDSL을 사용한 메서드들
public interface ReviewCustomRepository {
    public List<StoreDetailReviewResponse> findReviewsByStoreId(Long storeId, Pageable pageable);
}
