package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.review.MyReviewInfoResponse;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.twogather.twogatherwebbackend.domain.StoreStatus.APPROVED;
import static org.assertj.core.api.Assertions.*;


public class ReviewRepositoryTest extends RepositoryTest{
    private Member consumer1, consumer2, consumer3, consumer4;
    private Store store1, store2, store3;
    private Review review1;

    @BeforeEach
    void setUp() {
        consumer1 = memberRepository.save(
                Member.builder()
                        .username("뿡치1")
                        .email("a@a.a")
                        .password("123")
                        .name("user1")
                        .authenticationType(AuthenticationType.CONSUMER)
                        .isActive(true)
                        .build()
        );
        consumer2 = memberRepository.save(
                Member.builder()
                        .username("뿡치2")
                        .email("b@b.b")
                        .password("123")
                        .name("user2")
                        .authenticationType(AuthenticationType.CONSUMER)
                        .isActive(true)
                        .build()
        );
        consumer3 = memberRepository.save(
                Member.builder()
                        .username("뿡치3")
                        .email("c@c.c")
                        .password("123")
                        .name("user3")
                        .authenticationType(AuthenticationType.CONSUMER)
                        .isActive(true)
                        .build()
        );
        consumer4 = memberRepository.save(
                Member.builder()
                        .username("뿡치4")
                        .email("d@d.d")
                        .password("123")
                        .name("user4")
                        .authenticationType(AuthenticationType.CONSUMER)
                        .isActive(true)
                        .build()
        );
        store1 = storeRepository.save(
                Store.builder()
                        .storeId(1L)
                        .name("store1")
                        .address("addr1")
                        .phone("010")
                        .status(APPROVED)
                        .build()
        );
        store2 = storeRepository.save(Store.builder()
                .storeId(2L)
                .name("store2")
                .address("addr2")
                .phone("010")
                .status(APPROVED)
                .build()
        );
        store3 = storeRepository.save(Store.builder()
                .storeId(3L)
                .name("store3")
                .address("addr3")
                .phone("010")
                .status(APPROVED)
                .build()
        );

        // for store1
        review1 = reviewRepository.save(Review.builder().store(store1).reviewer(consumer1).content("맛있어용").score(5.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store1).reviewer(consumer2).content("괜찮아용").score( 4.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store1).reviewer(consumer3).content("맛있어용").score(5.0).createdDate(LocalDate.now()).build());

        // for store2
        reviewRepository.save(Review.builder().store(store2).reviewer(consumer1).content("맛없어용").score(1.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store2).reviewer(consumer2).content("맛있어용").score(5.0).createdDate(LocalDate.now()).build());

        // for store3
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content( "맛있어용").score(5.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer( consumer4).content( "맛있어용").score(5.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer( consumer4).content("맛있어용").score(5.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content( "맛있어용").score(5.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer( consumer4).content( "맛있어용").score(5.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content( "맛있어용").score(5.0).createdDate( LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content( "맛있어용").score( 5.0).createdDate( LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content( "맛있어용").score(5.0).createdDate( LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content( "맛있어용").score(5.0).createdDate( LocalDate.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content( "맛있어용").score(5.0).createdDate( LocalDate.now()).build());

        em.flush();
    }

    @Test
    @DisplayName("내가 작성한 리뷰 목록 조회")
    @Transactional
    void WhenRequestMyReviews_ThenGetReviewsByMyMemberId() {
        // given
        int page = 0;
        int size = 5;
        String orderColumn = "createdDate";
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderColumn).descending());

        // when
        Page<MyReviewInfoResponse> result1 = reviewRepository.findMyReviewsByMemberId(consumer1.getMemberId(), pageable);
        Page<MyReviewInfoResponse> result2 = reviewRepository.findMyReviewsByMemberId(consumer2.getMemberId(), pageable);
        Page<MyReviewInfoResponse> result3 = reviewRepository.findMyReviewsByMemberId(consumer3.getMemberId(), pageable);

        // then
        // 해당 리뷰들이 user1의 리뷰인지 확인
        result1.stream().map(
                review -> assertThat(review.getConsumerName())
                        .isEqualTo("user1")
        );
        // 해당 리뷰들이 user2의 리뷰인지 확인
        result2.stream().map(
                review -> assertThat(review.getConsumerName())
                        .isEqualTo("user2")
        );
        // 해당 리뷰들이 user3의 리뷰인지 확인
        result3.stream().map(
                review -> assertThat(review.getConsumerName())
                        .isEqualTo("user3")
        );
        assertThat(result1.getTotalElements()).isEqualTo(2);
        assertThat(result2.getTotalElements()).isEqualTo(2);
        assertThat(result3.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("리뷰 삭제 테스트")
    @Transactional
    void WhenDelete_ThenReviewSizeDecreases() {
        // given
        int page = 0;
        int size = 10;
        String orderColumn = "createdDate";
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderColumn).descending());

        // 리뷰 삭제 이전 store1의 리뷰 개수
        Long reviewSize = reviewRepository
                .findReviewsByStoreId(store1.getStoreId(), pageable).getTotalElements();

        // when
        reviewRepository.deleteById(review1.getReviewId());

        // 리뷰 삭제 이후 store1의 리뷰 개수
        Long reviewSizeAfterDelete = reviewRepository
                .findReviewsByStoreId(store1.getStoreId(), pageable).getTotalElements();

        // then
        assertThat(reviewSizeAfterDelete).isEqualTo(reviewSize - 1);
        assertThat(reviewRepository.findById(review1.getReviewId()).isPresent()).isFalse();
    }

    @Test
    @DisplayName("리뷰 목록 조회시 사용자의 평균 평점을 정상적으로 포함하여 응답하는지 테스트")
    @Transactional
    void WhenFindByStoreStoreId_ThenReturnReviewsWithAvgScoreOfCustomer() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        Map<Long, Double> map = new HashMap<>();    // consumer1,2,3의 평균평점 저장
        map.put(consumer1.getMemberId(), 3.0);
        map.put(consumer2.getMemberId(), 4.5);
        map.put(consumer3.getMemberId(), 5.0);

        // when
        Page<StoreDetailReviewResponse> results1 = reviewRepository.findReviewsByStoreId(store1.getStoreId(), pageable);
        List<StoreDetailReviewResponse> content = results1.getContent();

<<<<<<< HEAD
        // then
        content.stream().forEach(
                item -> {
                    assertThat(item.getConsumerAvgScore()).isEqualTo(map.get(item.getConsumerId()));
                    System.out.println(item.getReviewId());
                    System.out.println(item.getConsumerId());
                    System.out.println(item.getConsumerName());
                    System.out.println(item.getConsumerAvgScore());
                }
        );
=======
        assertThat(results1).isNotEmpty();
        assertThat(content.get(0).getConsumerAvgScore()).isEqualTo(3.0);   // user1의 평균 리뷰 평점
        assertThat(content.get(1).getConsumerAvgScore()).isEqualTo(4.5);   // user2의 평균 리뷰 평점
        assertThat(content.get(2).getConsumerAvgScore()).isEqualTo(5.0);   // user3의 평균 리뷰 평점
>>>>>>> d591cb6431895eeda3a9bb4a92bc6691973b93c5
    }

    @Test
    @DisplayName("가게 리뷰 목록 조회 - 첫페이지와 두번째 페이지에 대한 필드값이 예상한 값과 같아야한다")
    @Transactional
    void When12ReviewsOnStore3_ThenReturnMaximum5ReviewsForEachPage() {
        // given
        // store3의 총 review 수는 총 12개
        Pageable pageable0 = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        Pageable pageable1 = PageRequest.of(1, 5, Sort.by("createdDate").descending());
        Pageable pageable2 = PageRequest.of(2, 5, Sort.by("createdDate").descending());

        // when
        Page<StoreDetailReviewResponse> pagedResult0 = reviewRepository.findReviewsByStoreId(store3.getStoreId(), pageable0);
        Page<StoreDetailReviewResponse> pagedResult1 = reviewRepository.findReviewsByStoreId(store3.getStoreId(), pageable1);
        Page<StoreDetailReviewResponse> pagedResult2 = reviewRepository.findReviewsByStoreId(store3.getStoreId(), pageable2);

        // then
<<<<<<< HEAD
        assertThat(pagedResult0.getNumberOfElements()).isEqualTo(5);
        assertThat(pagedResult1.getNumberOfElements()).isEqualTo(5);
        assertThat(pagedResult2.getNumberOfElements()).isEqualTo(2);
=======
        assertThat(pagedResult0.getNumberOfElements()).isEqualTo(10);
        assertThat(pagedResult1.getNumberOfElements()).isEqualTo(2);
        assertThat(pagedResult0.getTotalElements()).isEqualTo(12);
>>>>>>> d591cb6431895eeda3a9bb4a92bc6691973b93c5
        assertThat(pagedResult0.isFirst()).isTrue();
        assertThat(pagedResult2.isLast()).isTrue();

        System.out.println(pagedResult0.getTotalElements());
    }

    @Test
    @DisplayName("사용자 리뷰 목록 조회 - 페이징 적용결과 각 페이지별 element의 개수가 제대로 출력돼야한다")
    @Transactional
    void WhenConsumer4Written12Reviews_ThenReturnMaximum5ReviewsForEachPage() {
        // given
        // consumer4의 총 review 수는 총 12개
        Pageable pageable0 = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        Pageable pageable1 = PageRequest.of(1, 5, Sort.by("createdDate").descending());
        Pageable pageable2 = PageRequest.of(2, 5, Sort.by("createdDate").descending());

        // when
        Page<MyReviewInfoResponse> pagedResult0 = reviewRepository.findMyReviewsByMemberId(consumer4.getMemberId(), pageable0);
        Page<MyReviewInfoResponse> pagedResult1 = reviewRepository.findMyReviewsByMemberId(consumer4.getMemberId(), pageable1);
        Page<MyReviewInfoResponse> pagedResult2 = reviewRepository.findMyReviewsByMemberId(consumer4.getMemberId(), pageable2);

        // then
<<<<<<< HEAD
        assertThat(pagedResult0.getNumberOfElements()).isEqualTo(5);
        assertThat(pagedResult1.getNumberOfElements()).isEqualTo(5);
        assertThat(pagedResult2.getNumberOfElements()).isEqualTo(2);
=======
        assertThat(pagedResult0.getContent().get(0).getContent()).isNotBlank();
        assertThat(pagedResult0.getContent().get(0).getConsumerName()).isNotBlank();
        assertThat(pagedResult0.getContent().get(0).getStoreName()).isNotBlank();
        //assertThat(pagedResult0.getContent().get(0).getUrl()).isNotBlank();
        assertThat(pagedResult0.getContent().get(0).getReviewId()).isNotNull();
        assertThat(pagedResult0.getNumberOfElements()).isEqualTo(10);
        assertThat(pagedResult1.getNumberOfElements()).isEqualTo(2);
        assertThat(pagedResult1.getNumberOfElements()).isEqualTo(2);
        assertThat(pagedResult0.getTotalElements()).isEqualTo(12);
>>>>>>> d591cb6431895eeda3a9bb4a92bc6691973b93c5
        assertThat(pagedResult0.isFirst()).isTrue();
        assertThat(pagedResult2.isLast()).isTrue();

        System.out.println(pagedResult0.getTotalElements());
    }
}
