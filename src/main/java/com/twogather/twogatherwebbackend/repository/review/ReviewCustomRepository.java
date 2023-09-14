package com.twogather.twogatherwebbackend.repository.review;

import com.twogather.twogatherwebbackend.dto.review.MyReviewInfoResponse;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public interface ReviewCustomRepository {
    Page<StoreDetailReviewResponse> findReviewsByStoreId(Long storeId, Pageable pageable);
    Page<MyReviewInfoResponse> findMyReviewsByMemberId(Long memberId, Pageable pageable);
}
