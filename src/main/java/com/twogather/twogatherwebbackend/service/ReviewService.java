package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.review.*;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.ReviewException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_MEMBER;
import static com.twogather.twogatherwebbackend.exception.ReviewException.ReviewErrorCode.NO_SUCH_REVIEW;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    public boolean isMyReview(Long reviewId) {
        String username = SecurityUtils.getLoginUsername();

        Member reviewer = memberRepository.findActiveMemberByUsername(username)
                .orElseThrow(() -> new MemberException(NO_SUCH_MEMBER));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NO_SUCH_REVIEW));

        if(!review.getReviewer().getMemberId().equals(reviewer.getMemberId())) {
            throw new ReviewException(NO_SUCH_REVIEW);
        }

        return true;
    }

    public ReviewResponse save(Long storeId, ReviewSaveUpdateRequest request) {
        String username = SecurityUtils.getLoginUsername();

        Member reviewer = memberRepository.findActiveMemberByUsername(username)
                .orElseThrow(() -> new MemberException(NO_SUCH_MEMBER));
        Store store =  storeRepository.findActiveStoreById(storeId)
                .orElseThrow(() -> new StoreException(NO_SUCH_STORE));

        Review result = reviewRepository.save(
                Review.builder()
                        .store(store)
                        .reviewer(reviewer)
                        .content(request.getContent())
                        .score(roundToNearestHalf(request.getScore()))
                        .createdDate(LocalDateTime.now())
                        .build());

        return toReviewResponse(result);
    }

    public ReviewResponse update(Long reviewId, ReviewSaveUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NO_SUCH_REVIEW));

        review.update(request.getContent(), roundToNearestHalf(request.getScore()));

        return toReviewResponse(review);
    }


    public void delete(Long reviewId){
        reviewRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public Page<MyReviewInfoResponse> getMyReviewInfos(Long memberId, Pageable pageable){
        return reviewRepository.findMyReviewsByMemberId(memberId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StoreDetailReviewResponse> getReviewsByStoreId(Long storeId, Pageable pageable) {
        return reviewRepository.findReviewsByStoreId(storeId, pageable);
    }

    private ReviewResponse toReviewResponse(Review review) {
        return new ReviewResponse(review.getReviewId(), review.getContent(),
                review.getScore(), review.getCreatedDate(), review.getReviewer().getName());
    }

    // 점수를 0.5단위로 반올림
    public Double roundToNearestHalf(Double input) {
        Double score = input;
        score = Math.round(score * 2) / 2.0;

        return score;
    }
}
