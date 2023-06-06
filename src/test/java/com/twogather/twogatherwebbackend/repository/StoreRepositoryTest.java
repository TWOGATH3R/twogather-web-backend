package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.StoreSearchType;
import com.twogather.twogatherwebbackend.dto.store.StoreResponseWithKeyword;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class StoreRepositoryTest extends RepositoryTest{

    @BeforeEach
    public void init(){
            store1 = storeRepository.save(new Store(null,"가게1","전주시 어쩌고어쩌고","063-231-4444", StoreStatus.APPROVED,null));
            store2 = storeRepository.save(new Store(null,"가게2", "서울특별시 서초구 신반포로23길 30 반원상가", "010-1234-1234", StoreStatus.APPROVED,null));
            store3 = storeRepository.save(new Store(null,"가게3", "서울특별시 서초구 올림픽대로 2085-14 솔빛섬 1-2F", "02-232-2222", StoreStatus.APPROVED,null));
            store4 = storeRepository.save(new Store(null,"가게4", "서울특별시 서초구 신반포로23길 30 반원상가", "02-232-2522", StoreStatus.APPROVED,null));

            Review review1 = reviewRepository.save(new Review(store1, null, "맛잇어요", 4.2, LocalDate.of(2020,02,02)));
            Review review2 = reviewRepository.save(new Review(store1, null, "위생이안좋군요", 4.2, LocalDate.of(2022,04,02)));

            Review review3 = reviewRepository.save(new Review(store2, null, "분위기가좋아요", 2.2, LocalDate.of(2021,01,12)));
            Review review4 = reviewRepository.save(new Review(store2, null, "아이들과 오기 좋네요", 2.2, LocalDate.of(2019,01,12)));

            Review review5 = reviewRepository.save(new Review(store3, null, "아이들과 오기 좋네요", 1.2, LocalDate.of(2019,01,12)));
            Review review6 = reviewRepository.save(new Review(store3, null, "분위기가좋아요", 1.4, LocalDate.of(2021,01,12)));
            Review review7 = reviewRepository.save(new Review(store3, null, "아이들과 오기 좋네요", 1.2, LocalDate.of(2019,01,12)));

            Review review8 = reviewRepository.save(new Review(store4, null, "아이들과 오기 좋네요", 3.2, LocalDate.of(2019,01,12)));

            em.flush();
    }
    @Test
    @DisplayName("평균리뷰점수/내림차순으로 잘 정렬이 되는지 확인")
    void whenFindTopNByScore_ShouldReturnTopNStoresByScore() {
        // When
        List<TopStoreResponse> topStores = storeRepository.findTopNByType(3, StoreSearchType.TOP_RATED.name(), "desc");
        // Then
        assertThat(topStores)
                .isNotEmpty()
                .noneMatch(response -> response.getStoreName().equals(store3.getName()))
                .allMatch(response -> !response.getStoreName().isBlank());
    }

    @Test
    @DisplayName("리뷰개수/내림차순으로 결과가 잘 정렬되는지확인")
    void whenFindTopNByReviewCount_ShouldNotReturnStore4() {
        // When
        List<TopStoreResponse> topStores = storeRepository.findTopNByType(3, StoreSearchType.MOST_REVIEWED.name(), "desc");
        // Then
        assertThat(topStores)
                .isNotEmpty()
                .noneMatch(response -> response.getStoreName().equals(store4.getName()))
                .allMatch(response -> !response.getStoreName().isEmpty());
    }


    @Test
    @DisplayName("좋아요수/내림차순으로 결과가 잘 나오는지 확인")
    void whenFindTopNByLikeCount_ShouldReturnFirstIsStore1() {
        //given
        Consumer consumer1 = consumerRepository.save(new Consumer("user1","dasd1@naver.com,",passwordEncoder.encode("sadad@123"), "name1", AuthenticationType.CONSUMER, true));
        likeRepository.save(new Likes(store1, consumer1));
        em.flush();
        em.clear();
        // When

        List<TopStoreResponse> topStores = storeRepository.findTopNByType(3, StoreSearchType.MOST_LIKES_COUNT.name(), "desc");
        // Then
        assertThat(topStores.get(0).getStoreName()).isEqualTo(store1.getName());

    }

    @Test
    @DisplayName("가게를 키워드, 지역, 카테고리로 검색하면 그에 해당하는 결과를 페이징을 사용해서 반환해준다")
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

        // when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, category, keyword, location);

        // Then
        assertThat(topStores).isNotEmpty();
        assertThat(topStores.getContent().stream()
                .filter(response -> response.getStoreName().equals(store1.getName()))
                .allMatch(response ->
                        response.getAddress().equals(store1.getAddress()) &&
                                response.getAvgScore().equals(4.2)));
    }


    @Test
    @DisplayName("가게를 지역, 카테고리로만 검색하면 키워드는 필터링에서 제외하고 결과를 반환해준다")
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

        // when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, category, emptyKeyword, location);

        // Then - return category2
        assertThat(topStores.getContent().stream()
                .filter(response -> response.getStoreName().equals(store2.getName()))
                .allMatch(response -> response.getAddress().equals(store2.getAddress()) &&
                        response.getStoreImageUrl().equals(image2.getUrl()) &&
                        response.getAvgScore() == 2.2))
                .isTrue();
    }


    @Test
    @DisplayName("가게를 키워드,카테고리로만 검색하면 지역은 필터링에서 제외하고 결과를 반환해준다")
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
