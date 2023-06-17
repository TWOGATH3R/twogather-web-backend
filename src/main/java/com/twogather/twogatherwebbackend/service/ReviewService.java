package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.dto.review.MyReviewInfoResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewSaveRequest;
import com.twogather.twogatherwebbackend.dto.review.ReviewUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    public boolean isMyReview(Long reviewId){
        //TODO:구현
        //new CustomAuthenticationException(UNAUTHORIZED)
        return false;
    }
    public ReviewResponse save(ReviewSaveRequest request){
        //TODO:구현
        return new ReviewResponse();
    }
    public ReviewResponse update(ReviewUpdateRequest request){
        //TODO:구현
        return new ReviewResponse();
    }
    public void delete(Long reviewId){
        //TODO:구현

    }
    public Page<MyReviewInfoResponse> getMyReviewInfos(Long memberId, Long storeId, Pageable pageable){
        //TODO:구현
        return null;
    }

}
