package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.dto.review.MyReviewInfoResponse;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewSaveRequest;
import com.twogather.twogatherwebbackend.dto.review.ReviewUpdateRequest;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public boolean isMyReview(Long reviewId){
        //TODO:구현
        return false;
    }
    public StoreDetailReviewResponse save(ReviewSaveRequest request){
        //TODO:구현
        return new StoreDetailReviewResponse();
    }
    public StoreDetailReviewResponse update(ReviewUpdateRequest request){
        //TODO:구현
        return new StoreDetailReviewResponse();
    }
    public void delete(Long reviewId){
        //TODO:구현

    }
    public Page<MyReviewInfoResponse> getMyReviewInfos(Long memberId, String orderBy, String orderColumn, int page, int size){
        //TODO:구현
        return null;
    }

    public Page<StoreDetailReviewResponse> getReviewsByStoreId(Long storeId, String orderBy,
                                                            String orderColumn, int page, int size) {
        Pageable pageable;
        
        if(orderBy.equals("desc")) {
            // 내림차순(default)
            pageable = PageRequest.of(page, size, Sort.by(orderColumn).descending());
        } else {
            // 오름차순
            pageable = PageRequest.of(page, size, Sort.by(orderColumn));
        }

        List<StoreDetailReviewResponse> result = reviewRepository.findReviewsByStoreId(storeId, pageable);

        int resSize = result.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), size);

        return new PageImpl<>(result.subList(start, end), pageable, size);
    }
}
