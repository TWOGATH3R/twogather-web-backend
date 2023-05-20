package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.config.QueryDslConfig;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreApprovalStatus;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(QueryDslConfig.class)
@SpringBootTest
public class StoreRepositoryTest {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private EntityManager em;

    private Store store1;
    private Store store2;
    private Store store3;
    private Store store4;

    @BeforeEach
    void setUp() {
        store1 = storeRepository.save(new Store(null,"가게1","전주시 어쩌고어쩌고","063-231-4444", StoreApprovalStatus.APPROVED,null));
        store2 = storeRepository.save(new Store(null,"가게2", "서울시 어쩌고 어저고", "010-1234-1234",StoreApprovalStatus.APPROVED,null));
        store3 = storeRepository.save(new Store(null,"가게3", "대전광역시 어쩌고 어쩌고", "02-232-2222",StoreApprovalStatus.APPROVED,null));
        store4 = storeRepository.save(new Store(null,"가게4", "서울시 어쩌고 어쩌고", "02-232-2522", StoreApprovalStatus.APPROVED,null));

        Review review1 = reviewRepository.save(new Review(store1, null, "맛잇어요", 4.2, LocalDate.of(2020,02,02)));
        Review review2 = reviewRepository.save(new Review(store1, null, "위생이안좋군요", 2.2, LocalDate.of(2022,04,02)));

        Review review3 = reviewRepository.save(new Review(store2, null, "분위기가좋아요", 4.4, LocalDate.of(2021,01,12)));
        Review review4 = reviewRepository.save(new Review(store2, null, "아이들과 오기 좋네요", 3.2, LocalDate.of(2019,01,12)));

        Review review5 = reviewRepository.save(new Review(store3, null, "아이들과 오기 좋네요", 1.2, LocalDate.of(2019,01,12)));
        Review review6 = reviewRepository.save(new Review(store3, null, "분위기가좋아요", 1.4, LocalDate.of(2021,01,12)));
        Review review7 = reviewRepository.save(new Review(store3, null, "아이들과 오기 좋네요", 1.2, LocalDate.of(2019,01,12)));

        Review review8 = reviewRepository.save(new Review(store4, null, "아이들과 오기 좋네요", 4.2, LocalDate.of(2019,01,12)));

        //store4: 리뷰가 가장적다
        //store3: 평점이 가장 적다
        em.flush();
    }


    @Test
    @DisplayName("findTopNByScore should return the top N stores by score")
    @Transactional
    void findTopNByScore_ShouldReturnTopNStoresByScore() {
        // When
        List<TopStoreResponse> topStores = storeRepository.findTopNByScore(3);
        // Then
        assertThat(topStores).isNotEmpty();

        //store3는 리뷰가 가장 적다
        for (TopStoreResponse response: topStores){
            assertThat(response.getStoreName()).isNotEqualTo(store3.getName());
            assertThat(response.getStoreName()).isNotBlank();
        }
    }

    @Test
    @DisplayName("findTopNByReviewCount should return the top N stores by review count")
    @Transactional
    void findTopNByReviewCount_ShouldReturnTopNStoresByReviewCount() {
        // When
        List<TopStoreResponse> topStores = storeRepository.findTopNByReviewCount(3);

        // Then
        assertThat(topStores).isNotEmpty();
        //store4는 리뷰가 가장 적다
        for (TopStoreResponse response: topStores){
            assertThat(response.getStoreName()).isNotEqualTo(store4.getName());
            assertThat(response.getStoreName()).isNotBlank();
        }
    }
}
