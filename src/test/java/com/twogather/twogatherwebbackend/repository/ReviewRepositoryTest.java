package com.twogather.twogatherwebbackend.repository;

import com.amazonaws.services.s3.model.Owner;
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
import java.time.LocalDateTime;
import java.util.*;

import static com.twogather.twogatherwebbackend.domain.StoreStatus.APPROVED;
import static org.assertj.core.api.Assertions.*;


public class ReviewRepositoryTest extends RepositoryTest{
    private Consumer consumer1, consumer2, consumer3, consumer4, consumer5;
    private StoreOwner owner1;
    private Store store1, store2, store3;
    private Review review1;
    private Comment comment1;

    @BeforeEach
    void setUp() {
        consumer1 = consumerRepository.save(new Consumer("user1", "a@a.a", "123", "뿡치1", AuthenticationType.CONSUMER, true));
        consumer2 = consumerRepository.save(new Consumer("user2", "b@b.b", "123", "뿡치2", AuthenticationType.CONSUMER, true));
        consumer3 = consumerRepository.save(new Consumer("user3", "c@c.c", "123", "뿡치3", AuthenticationType.CONSUMER, true));
        consumer4 = consumerRepository.save(new Consumer("user4", "d@d.d", "123", "뿡치4", AuthenticationType.CONSUMER, true));
        consumer5 = consumerRepository.save(new Consumer("user5", "e@e.e", "123", "뿡치5", AuthenticationType.CONSUMER, true));
        owner1 = memberRepository.save(new StoreOwner("owner1", "1@1.1", "123", "주인1", AuthenticationType.STORE_OWNER, true));

        store1 = storeRepository.save(
                Store.builder()
                        .name("store1")
                        .address("addr1")
                        .phone("010")
                        .status(APPROVED)
                        .build()
        );
        store2 = storeRepository.save(Store.builder()
                .name("store2")
                .address("addr2")
                .phone("010")
                .status(APPROVED)
                .build()
        );
        store3 = storeRepository.save(Store.builder()
                .name("store3")
                .address("addr3")
                .phone("010")
                .status(APPROVED)
                .build()
        );

        Store tempStore = Store.builder()
                .name("store4")
                .address("addr4")
                .phone("010")
                .status(APPROVED)
                .build();
        Store store4 = storeRepository.save(tempStore);
        imageRepository.save(new Image(store4, "url1"));
        imageRepository.save(new Image(store4, "url2"));
        imageRepository.save(new Image(store4, "url3"));

        // for store1
        review1 = reviewRepository.save(Review.builder().store(store1).reviewer(consumer1).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store1).reviewer(consumer2).content("괜찮아용").score(4.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store1).reviewer(consumer3).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());

        // for store2
        reviewRepository.save(Review.builder().store(store2).reviewer(consumer1).content("맛없어용").score(1.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store2).reviewer(consumer2).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());

        // for store3
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());
        reviewRepository.save(Review.builder().store(store3).reviewer(consumer4).content("맛있어용").score(5.0).createdDate(LocalDateTime.now()).build());

        // for store4
        reviewRepository.save(Review.builder().store(store4).reviewer(consumer5).content("이미지테스트").score(5.0).createdDate(LocalDateTime.now()).build());
        
        em.flush();
    }

    @Test
    @DisplayName("comment를 정상적으로 불러오는지 테스트")
    @Transactional
    void commentTest() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        em.clear();

        // when
        Comment savedComment = commentRepository.save(new Comment("감사합니당^^", review1, owner1));
        Page<StoreDetailReviewResponse> response = reviewRepository.findReviewsByStoreId(store1.getStoreId(), pageable);

        // then
        System.out.println("=====================================");
        System.out.println(response.getContent().get(0).getComment().getContent());
        System.out.println(response.getContent().get(1).getComment().getContent());
        System.out.println(response.getContent().get(2).getComment().getContent());
        System.out.println("=====================================");
    }

    @Test
    @DisplayName("이미지 목록 테스트")
    @Transactional
    void imageTest() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        
        // consumer5는 store4에 리뷰 한개 작성
        // store4는 3개의 이미지를 가짐
        em.clear();
        Page<MyReviewInfoResponse> response = reviewRepository.findMyReviewsByMemberId(consumer5.getMemberId(), pageable);

        System.out.println("=====================================");
        System.out.println(response.getContent().get(0).getUrl());
        System.out.println(response.getContent().get(1).getUrl());
        System.out.println(response.getContent().get(2).getUrl());
        System.out.println("=====================================");
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

        Map<String, Double> map = new HashMap<>();
        map.put("뿡치1", 3.0);  // user1의 평균 리뷰 평점
        map.put("뿡치2", 4.5);  // user2의 평균 리뷰 평점
        map.put("뿡치3", 5.0);  // user3의 평균 리뷰 평점

        // when
        Page<StoreDetailReviewResponse> results1 = reviewRepository.findReviewsByStoreId(store1.getStoreId(), pageable);
        List<StoreDetailReviewResponse> content = results1.getContent();

        // then
        assertThat(content.get(0).getConsumerAvgScore()).isEqualTo(map.get(content.get(0).getConsumerName()));
        assertThat(content.get(1).getConsumerAvgScore()).isEqualTo(map.get(content.get(1).getConsumerName()));
        assertThat(content.get(2).getConsumerAvgScore()).isEqualTo(map.get(content.get(2).getConsumerName()));
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
        assertThat(pagedResult0.getNumberOfElements()).isEqualTo(5);
        assertThat(pagedResult1.getNumberOfElements()).isEqualTo(5);
        assertThat(pagedResult2.getNumberOfElements()).isEqualTo(2);
        assertThat(pagedResult0.getTotalElements()).isEqualTo(12);
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
        assertThat(pagedResult0.getNumberOfElements()).isEqualTo(5);
        assertThat(pagedResult1.getNumberOfElements()).isEqualTo(5);
        assertThat(pagedResult2.getNumberOfElements()).isEqualTo(2);
        assertThat(pagedResult0.getContent().get(0).getContent()).isNotBlank();
        assertThat(pagedResult0.getContent().get(0).getConsumerName()).isNotBlank();
        assertThat(pagedResult0.getContent().get(0).getStoreName()).isNotBlank();
        //assertThat(pagedResult0.getContent().get(0).getUrl()).isNotBlank();
        assertThat(pagedResult0.getContent().get(0).getReviewId()).isNotNull();
        assertThat(pagedResult0.getTotalElements()).isEqualTo(12);
        assertThat(pagedResult0.isFirst()).isTrue();
        assertThat(pagedResult2.isLast()).isTrue();

        System.out.println(pagedResult0.getTotalElements());
    }
}
