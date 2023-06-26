package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.dto.review.ReviewResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewSaveUpdateRequest;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class) // Mockito 사용 명시
public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private StoreRepository storeRepository;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewService = new ReviewService(reviewRepository, memberRepository, storeRepository);
    }

    @Test
    @DisplayName("리뷰 등록 시 0.5 단위로 점수를 입력하지 않을 시 반올림하여 0.5 단위로 만듦")
    public void WhenRequestScoreIsNot_ThenChangeScore() {
        // given
        Double score1 = 3.2;
        Double score2 = 1.4;

        // when
        Double result1 = reviewService.roundToNearestHalf(score1);
        Double result2 = reviewService.roundToNearestHalf(score2);

        // then
        assertThat(result1).isEqualTo(3.0);
        assertThat(result2).isEqualTo(1.5);
    }
}
