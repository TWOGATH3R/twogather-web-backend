package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.config.QueryDslConfig;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.store.StoreResponseWithKeyword;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(QueryDslConfig.class)
@SpringBootTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.default_batch_fetch_size=100")
public class StoreRepositoryTest {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private StoreKeywordRepository storeKeywordRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

    private Store store1;
    private Store store2;
    private Store store3;
    private Store store4;

    @BeforeEach
    void setUp() {
        store1 = storeRepository.save(new Store(null,"가게1","전주시 어쩌고어쩌고","063-231-4444", StoreApprovalStatus.APPROVED,null));
        store2 = storeRepository.save(new Store(null,"가게2", "서울특별시 서초구 신반포로23길 30 반원상가", "010-1234-1234",StoreApprovalStatus.APPROVED,null));
        store3 = storeRepository.save(new Store(null,"가게3", "서울특별시 서초구 올림픽대로 2085-14 솔빛섬 1-2F", "02-232-2222",StoreApprovalStatus.APPROVED,null));
        store4 = storeRepository.save(new Store(null,"가게4", "서울특별시 서초구 신반포로23길 30 반원상가", "02-232-2522", StoreApprovalStatus.APPROVED,null));

        Review review1 = reviewRepository.save(new Review(store1, null, "맛잇어요", 4.2, LocalDate.of(2020,02,02)));
        Review review2 = reviewRepository.save(new Review(store1, null, "위생이안좋군요", 4.2, LocalDate.of(2022,04,02)));

        Review review3 = reviewRepository.save(new Review(store2, null, "분위기가좋아요", 2.2, LocalDate.of(2021,01,12)));
        Review review4 = reviewRepository.save(new Review(store2, null, "아이들과 오기 좋네요", 2.2, LocalDate.of(2019,01,12)));

        Review review5 = reviewRepository.save(new Review(store3, null, "아이들과 오기 좋네요", 1.2, LocalDate.of(2019,01,12)));
        Review review6 = reviewRepository.save(new Review(store3, null, "분위기가좋아요", 1.4, LocalDate.of(2021,01,12)));
        Review review7 = reviewRepository.save(new Review(store3, null, "아이들과 오기 좋네요", 1.2, LocalDate.of(2019,01,12)));

        Review review8 = reviewRepository.save(new Review(store4, null, "아이들과 오기 좋네요", 3.2, LocalDate.of(2019,01,12)));

        //store4: 리뷰가 가장적다
        //store3: 평점이 가장 적다

        em.flush();
        //em.clear(); 주석설정해야 테스트가 성공. clear 해버리면 영속성컨텍스트가 비워져서 변경감지안됨
    }


    @Test
    @DisplayName("findTopNByScore should return the top N stores by score")
    @Transactional
    void whenFindTopNByScore_ShouldReturnTopNStoresByScore() {
        // When
        List<TopStoreResponse> topStores = storeRepository.findTopNByScore(3);
        // Then
        assertThat(topStores).isNotEmpty();

        //store3는 평균 점수가 가장 적다
        for (TopStoreResponse response: topStores){
            assertThat(response.getStoreName()).isNotEqualTo(store3.getName());
            assertThat(response.getStoreName()).isNotBlank();
        }
    }

    @Test
    @DisplayName("findTopNByReviewCount should return the top N stores by review count")
    @Transactional
    void whenFindTopNByReviewCount_ShouldReturnTopNStoresByReviewCount() {
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

    @Test
    @DisplayName("가게를 키워드, 지역, 카테고리로 검색하면 그에 해당하는 결과를 페이징을 사용해서 반환해준다")
    @Transactional
    void WhenSearchStoresWithKeywordLocationCategory_ThenReturnStore1() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "reviewsCount");
        Keyword keyword1 = keywordRepository.save(new Keyword("감성 있는"));
        Keyword keyword2 = keywordRepository.save(new Keyword("맛있는"));
        Keyword keyword3 = keywordRepository.save(new Keyword("분위기 좋은"));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword2));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword3));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword1));
        storeKeywordRepository.save(new StoreKeyword(store2, keyword1));

        Category category1 = categoryRepository.save(new Category("양식"));
        Category category2 = categoryRepository.save(new Category("한식"));
        store1.setCategory(category1);
        store2.setCategory(category2);
        store3.setCategory(category1);


        Image image1 = imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje1/(store1)"));
        imageRepository.save(new Image(store2, "http:s3.sae2/kjhkje2/(store2)"));
        imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje3/(store1)"));

        em.flush();
        em.clear();

        String keyword = keyword1.getName();
        String location = "전주시";
        String category = store1.getCategory().getName();

        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, category,keyword,location);

        // Then
        assertThat(topStores).isNotEmpty();
        assertThat(topStores.getContent().get(0).getStoreName()).isEqualTo(store1.getName());
        assertThat(topStores.getContent().get(0).getAddress()).isEqualTo(store1.getAddress());
        assertThat(topStores.getContent().get(0).getAvgScore()).isEqualTo(4.2);

    }

    @Test
    @DisplayName("가게를 지역, 카테고리로만 검색하면 키워드는 필터링에서 제외하고 결과를 반환해준다")
    @Transactional
    void WhenSearchStoresWithLocationCategoryExcludeKeyword_ThenReturnStore2WithAllKeywords() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "reviewsCount");
        Keyword keyword1 = keywordRepository.save(new Keyword("감성 있는"));
        Keyword keyword2 = keywordRepository.save(new Keyword("맛있는"));
        Keyword keyword3 = keywordRepository.save(new Keyword("분위기 좋은"));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword2));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword3));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword1));
        storeKeywordRepository.save(new StoreKeyword(store2, keyword1));

        Category category1 = categoryRepository.save(new Category("양식"));
        Category category2 = categoryRepository.save(new Category("한식"));
        store1.setCategory(category1);
        store2.setCategory(category2);
        store3.setCategory(category1);


        imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje1/(store1)"));
        Image image2 = imageRepository.save(new Image(store2, "http:s3.sae2/kjhkje2/(store2)"));
        imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje3/(store1)"));

        em.flush();
        em.clear();
        String emptyKeyword = "";
        String location = "서초구";
        String category = category2.getName();

        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, category,emptyKeyword,location);

        // Then - return category2
        assertThat(topStores.getContent().size()).isEqualTo(1);
        assertThat(topStores.getContent().get(0).getStoreName()).isEqualTo(store2.getName());
        assertThat(topStores.getContent().get(0).getAddress()).isEqualTo(store2.getAddress());
        assertThat(topStores.getContent().get(0).getStoreImageUrl()).isEqualTo(image2.getUrl());
        assertThat(topStores.getContent().get(0).getAvgScore()).isEqualTo(2.2);

    }

    @Test
    @DisplayName("가게를 키워드,카테고리로만 검색하면 지역은 필터링에서 제외하고 결과를 반환해준다")
    @Transactional
    void WhenSearchStoresWithKeywordCategoryExcludeLocation_ThenReturnStore1WithAllLocations() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "reviewsCount");
        Keyword keyword1 = keywordRepository.save(new Keyword("감성 있는"));
        Keyword keyword2 = keywordRepository.save(new Keyword("맛있는"));
        Keyword keyword3 = keywordRepository.save(new Keyword("분위기 좋은"));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword2));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword3));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword1));
        storeKeywordRepository.save(new StoreKeyword(store2, keyword1));

        Category category1 = categoryRepository.save(new Category("양식"));
        Category category2 = categoryRepository.save(new Category("한식"));
        store1.setCategory(category1);
        store2.setCategory(category2);
        store3.setCategory(category1);


        Image image1 = imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje1/(store1)"));
        imageRepository.save(new Image(store2, "http:s3.sae2/kjhkje2/(store2)"));
        imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje3/(store1)"));

        em.flush();
        em.clear();//이거 주석처리하면 n관계인 entity들이 안불러와짐 왜그러지?

        String keyword = keyword3.getName();
        String emptyLocation = "";
        String category = category1.getName();

        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, category,keyword,emptyLocation);

        // Then
        assertThat(topStores).isNotEmpty();
        assertThat(topStores.getContent().get(0).getStoreName()).isEqualTo(store1.getName());
        assertThat(topStores.getContent().get(0).getAddress()).isEqualTo(store1.getAddress());
        assertThat(topStores.getContent().get(0).getStoreImageUrl()).isEqualTo(image1.getUrl());
        assertThat(topStores.getContent().get(0).getAvgScore()).isEqualTo(4.2);

    }

    @Test
    @DisplayName("가게를 키워드,지역로만 검색하면 카테고리는 필터링에서 제외하고 결과를 반환해준다")
    @Transactional
    void WhenSearchStoresWithKeywordLocationExcludeCategory_ThenReturnStore1WithAllCategories() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "reviewsCount");
        Keyword keyword1 = keywordRepository.save(new Keyword("감성 있는"));
        Keyword keyword2 = keywordRepository.save(new Keyword("맛있는"));
        Keyword keyword3 = keywordRepository.save(new Keyword("분위기 좋은"));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword2));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword3));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword1));
        storeKeywordRepository.save(new StoreKeyword(store2, keyword1));

        Category category1 = categoryRepository.save(new Category("양식"));
        Category category2 = categoryRepository.save(new Category("한식"));
        store1.setCategory(category1);
        store2.setCategory(category2);
        store3.setCategory(category1);


        Image image1 = imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje1/(store1)"));
        imageRepository.save(new Image(store2, "http:s3.sae2/kjhkje2/(store2)"));
        imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje3/(store1)"));

        em.flush();
        em.clear();

        String keyword = keyword3.getName();
        String location = "전주시";
        String emptyCategory = "";


        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,keyword,location);

        // Then
        assertThat(topStores).isNotEmpty();
        assertThat(topStores.getContent().get(0).getStoreName()).isEqualTo(store1.getName());
        assertThat(topStores.getContent().get(0).getAddress()).isEqualTo(store1.getAddress());
        assertThat(topStores.getContent().get(0).getStoreImageUrl()).isEqualTo(image1.getUrl());
        assertThat(topStores.getContent().get(0).getAvgScore()).isEqualTo(4.2);

    }

    @Test
    @DisplayName("가게를 키워드,지역, 카테고리 필터링 제외시키고 결과리스트의 정렬됨만을 확인 - reviewCount, 내림차순")
    @Transactional
    void whenSortingParametersPassed_thenFindStoresByReviewCountDESCSortsResults() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reviewsCount");

        reviewRepository.save(new Review(store1, null, "맛잇어요", 4.2, LocalDate.of(2020,02,02)));
        reviewRepository.save(new Review(store1, null, "위생이안좋군요", 2.2, LocalDate.of(2022,04,02)));

        String keyword = "";
        String location = "";
        String emptyCategory = "";

        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,keyword,location);

        // Then -
        assertThat(topStores.getContent().get(0).getStoreName()).isEqualTo(store1.getName());
        assertThat(topStores.getContent().get(1).getStoreName()).isEqualTo(store3.getName());
        assertThat(topStores.getContent().get(2).getStoreName()).isEqualTo(store2.getName());
        assertThat(topStores.getContent().get(3).getStoreName()).isEqualTo(store4.getName());

    }

    @Test
    @DisplayName("가게를 키워드,지역,카테고리 필터링 제외시키고 결과리스트의 정렬됨만을 확인 - reviewCount, 오름차순")
    @Transactional
    void whenSortingParametersPassed_thenFindStoresByReviewCountASCCSortsResults() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "reviewsCount");

        reviewRepository.save(new Review(store1, null, "맛잇어요", 4.2, LocalDate.of(2020,02,02)));
        reviewRepository.save(new Review(store1, null, "위생이안좋군요", 2.2, LocalDate.of(2022,04,02)));

        String keyword = "";
        String location = "";
        String emptyCategory = "";

        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,keyword,location);

        // Then -
        assertThat(topStores.getContent().get(0).getStoreName()).isEqualTo(store4.getName());
        assertThat(topStores.getContent().get(1).getStoreName()).isEqualTo(store2.getName());
        assertThat(topStores.getContent().get(2).getStoreName()).isEqualTo(store3.getName());
        assertThat(topStores.getContent().get(3).getStoreName()).isEqualTo(store1.getName());

    }

    @Test
    @DisplayName("가게를 키워드,지역,카테고리 필터링 제외시키고 결과리스트의 정렬됨만을 확인 - avgScore, 내림차순")
    @Transactional
    void whenSortingParametersPassed_thenFindStoresByAvgScoreDESCSortsResults() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "avgScore");

        String emptyKeyword = "";
        String emptyLocation = "";
        String emptyCategory = "";

        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,emptyKeyword,emptyLocation);

        // Then
        for (int i = 1; i < topStores.getContent().size(); i++) {
            assertThat(topStores.getContent().get(i).getAvgScore())
                    .isGreaterThanOrEqualTo(
                            topStores.getContent().get(i - 1).getAvgScore()
                    );
        }

    }

    @Test
    @DisplayName("가게를 키워드,지역,카테고리 필터링 제외시키고 결과리스트의 정렬됨만을 확인 - avgScore, 오름차순")
    @Transactional
    void whenSortingParametersPassed_thenFindStoresByAvgScoreASCSortsResults() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "avgScore");

        String emptyKeyword = "";
        String emptyLocation = "";
        String emptyCategory = "";


        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,emptyKeyword,emptyLocation);

        // Then
        for (int i = 1; i < topStores.getContent().size(); i++) {
            assertThat(
                    topStores.getContent().get(i).getAvgScore())
                    .isLessThanOrEqualTo(
                            topStores.getContent().get(i - 1).getAvgScore()
                    );
        }
    }

}
