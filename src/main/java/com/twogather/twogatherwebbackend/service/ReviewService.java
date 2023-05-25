package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.dto.review.MyReviewInfoResponse;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewSaveRequest;
import com.twogather.twogatherwebbackend.dto.review.ReviewUpdateRequest;
import com.twogather.twogatherwebbackend.repository.ReviewRepository;
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
        Page<Review> reviews;
        List<StoreDetailReviewResponse> responseList = new ArrayList<>();
        Pageable pageable;
        
        if(orderBy.equals("desc")) {
            // 내림차순(default)
            pageable = PageRequest.of(page, size, Sort.by(orderColumn).descending());
        } else {
            // 오름차순
            pageable = PageRequest.of(page, size, Sort.by(orderColumn));
        }

        reviews = reviewRepository.findByStoreStoreId(storeId, pageable);

        // Review 객체들을 StoreDetailReviewResponse로 변환
        for(Review review : reviews) {
            responseList.add(new StoreDetailReviewResponse(review.getReviewer().getMemberId(), review.getReviewId(),
                    review.getContent(), review.getScore(), review.getCreatedDate(), review.getReviewer().getName()));
        }
        
        // PageImpl 구현체를 사용해 Page 반환
        return new PageImpl<>(responseList, pageable, reviews.getTotalElements());
    }
}
