package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.review.*;
import com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.ReviewException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException.AccessDeniedExceptionErrorCode.ACCESS_DENIED;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_MEMBER;
import static com.twogather.twogatherwebbackend.exception.ReviewException.ReviewErrorCode.NO_SUCH_REVIEW;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ConsumerRepository consumerRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    public boolean isMyReview(Long reviewId) {
        String username = SecurityUtils.getUsername();

        Member reviewer = memberRepository.findActiveMemberByUsername(username)
                .orElseThrow(() -> new MemberException(NO_SUCH_MEMBER));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NO_SUCH_REVIEW));

        if(!review.getReviewer().getMemberId().equals(reviewer.getMemberId())) {
            throw new CustomAccessDeniedException(ACCESS_DENIED);
        }

        return true;
    }

    public ReviewResponse save(ReviewSaveRequest request) {
        String username = SecurityUtils.getUsername();

        Member reviewer = memberRepository.findActiveMemberByUsername(username)
                .orElseThrow(() -> new MemberException(NO_SUCH_MEMBER));
        Store store =  storeRepository.findActiveStoreById(request.getStoreId())
                .orElseThrow(() -> new StoreException(NO_SUCH_STORE));

        Review result = reviewRepository.save(new Review(store, reviewer,
                request.getContent(), request.getScore(), LocalDate.now()));

        return toReviewResponse(result);
    }

    @Transactional
    public ReviewResponse update(ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new ReviewException(NO_SUCH_REVIEW));

        review.update(request.getContent(), request.getScore());

        return toReviewResponse(review);
    }

    public void delete(Long reviewId){
        reviewRepository.deleteById(reviewId);
    }

    public Page<MyReviewInfoResponse> getMyReviewInfos(Long memberId, Pageable pageable){
        return reviewRepository.findMyReviewsByMemberId(memberId, pageable);
    }

    public Page<StoreDetailReviewResponse> getReviewsByStoreId(Long storeId, Pageable pageable) {
        return reviewRepository.findReviewsByStoreId(storeId, pageable);
    }

    public ReviewResponse toReviewResponse(Review review) {
        return new ReviewResponse(review.getReviewId(), review.getContent(),
                review.getScore(), review.getCreatedDate(), review.getReviewer().getName());
    }
}
