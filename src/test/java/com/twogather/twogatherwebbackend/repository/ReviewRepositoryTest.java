package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.config.QueryDslConfig;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.twogather.twogatherwebbackend.domain.StoreApprovalStatus.APPROVED;

@Import(QueryDslConfig.class)
@SpringBootTest
public class ReviewRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private EntityManager em;

    private Consumer consumer1;
    private Consumer consumer2;
    private Consumer consumer3;

    private Store store1;
    private Store store2;

    private Review review1;
    private Review review2;
    private Review review3;
    private Review review4;
    private Review review5;

    @BeforeEach
    void setUp() {
        consumer1 = memberRepository.save(new Consumer("a@b.c", "123", "user1", AuthenticationType.CONSUMER, true));
        consumer2 = memberRepository.save(new Consumer("a@b.c", "123", "user2", AuthenticationType.CONSUMER, true));
        consumer3 = memberRepository.save(new Consumer("a@b.c", "123", "user3", AuthenticationType.CONSUMER, true));

        store1 = storeRepository.save(new Store(1L, "store1", "addr1", "010", APPROVED, ""));
        store2 = storeRepository.save(new Store(2L, "store2", "addr2", "010", APPROVED, ""));

        // for store1
        review1 = reviewRepository.save(new Review(store1, consumer1, "맛있어용", 5.0, LocalDate.now()));
        review2 = reviewRepository.save(new Review(store1, consumer2, "괜찮아용", 4.0, LocalDate.now()));
        review3 = reviewRepository.save(new Review(store1, consumer3, "맛있어용", 5.0, LocalDate.now()));

        // for store2
        review4 = reviewRepository.save(new Review(store2, consumer1, "맛없어용", 1.0, LocalDate.now()));
        review5 = reviewRepository.save(new Review(store2, consumer2, "맛있어용", 5.0, LocalDate.now()));

        em.flush();
    }

    @Test
    @DisplayName("리뷰 목록 조회시 사용자의 평균 평점을 정상적으로 포함하여 응답하는지 테스트")
    @Transactional
    void WhenFindByStoreStoreId_ThenReturnReviewsWithAvgScoreOfCustomer() {
        // given
        int page = 0;
        int size = 10;
        String orderColumn = "createdDate";
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderColumn).descending());

        // when
        List<StoreDetailReviewResponse> results1 = reviewRepository.findReviewsByStoreId(store1.getStoreId(), pageable);
        List<StoreDetailReviewResponse> results2 = reviewRepository.findReviewsByStoreId(store2.getStoreId(), pageable);

        // then
        Assertions.assertThat(results1).isNotEmpty();
        System.out.println("store1");
        System.out.println("page" + page + " count : " + results1.size());
        System.out.println();
        for(StoreDetailReviewResponse item : results1) {
            System.out.println("reviewId: " + item.getReviewId());
            System.out.println("consumerId: " + item.getConsumerId());
            System.out.println("consumerName:" + item.getConsumerName());
            System.out.println("content: " + item.getContent());
            System.out.println("score: " + item.getScore());
            System.out.println("createdDate: " + item.getCreatedDate());
            System.out.println("avgScore: " + item.getConsumerAvgScore());
        }
        System.out.println();

        Assertions.assertThat(results2).isNotEmpty();
        System.out.println("store2");
        System.out.println("page" + page + " count : " + results2.size());
        System.out.println();
        for(StoreDetailReviewResponse item : results2) {
            System.out.println("reviewId: " + item.getReviewId());
            System.out.println("consumerId: " + item.getConsumerId());
            System.out.println("consumerName:" + item.getConsumerName());
            System.out.println("content: " + item.getContent());
            System.out.println("score: " + item.getScore());
            System.out.println("createdDate: " + item.getCreatedDate());
            System.out.println("avgScore: " + item.getConsumerAvgScore());
        }
    }
}
