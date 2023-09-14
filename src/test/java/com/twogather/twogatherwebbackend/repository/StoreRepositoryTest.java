package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.store.StoreSearchType;
import com.twogather.twogatherwebbackend.dto.store.MyLikeStoreResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreDefaultResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreResponseWithKeyword;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.util.TestConstants.CONSUMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class StoreRepositoryTest extends RepositoryTest{

    @BeforeEach
    public void init(){
        Category category = categoryRepository.save(Category.builder().name("퓨전").build());
        store1 = storeRepository.save(Store.builder().name("가게1").address("전주시 어쩌고 어저고").phone("063-231-4444").status(StoreStatus.APPROVED).category(category).build());
        store2 = storeRepository.save(Store.builder().name("가게2").address("서울시 서초구 어저고").phone("010-1234-1234").status(StoreStatus.APPROVED).category(category).build());
        store3 = storeRepository.save(Store.builder().name("가게3").address("서울특별시 서초구 신반포로23길 30 반원상가").phone("02-232-2222").category(category).status(StoreStatus.APPROVED).build());
        store4 = storeRepository.save(Store.builder().name("가게4").address("서울시 서초구 어쩌고").phone("063-231-4444").category(category).status(StoreStatus.APPROVED).build());

        Review review1 = reviewRepository.save(Review.builder().content("맛잇어요").score(4.2).createdDate(LocalDateTime.now()).build());
        Review review2 = reviewRepository.save(Review.builder().content("위생이안좋군요").score(4.2).createdDate(LocalDateTime.now()).build());
        review1.addStore(store1);
        review2.addStore(store1);

        Review review3 = reviewRepository.save(Review.builder().content("분위기가좋아요").score(2.2).createdDate(LocalDateTime.now()).build());
        Review review4 = reviewRepository.save(Review.builder().content("아이들과 오기 좋네요").score(2.2).createdDate(LocalDateTime.now()).build());
        review3.addStore(store2);
        review4.addStore(store2);

        Review review5 = reviewRepository.save(Review.builder().content("아이들과 오기 좋네요").score(1.2).createdDate(LocalDateTime.now()).build());
        Review review6 = reviewRepository.save(Review.builder().content("분위기가좋아요").score(1.4).createdDate(LocalDateTime.now()).build());
        Review review7 = reviewRepository.save(Review.builder().content("아이들과 오기 좋네요").score(1.2).createdDate(LocalDateTime.now()).build());
        review5.addStore(store3);
        review6.addStore(store3);
        review7.addStore(store3);

        Review review8 = reviewRepository.save(Review.builder().content("아이들과 오기 좋네요").score(3.2).createdDate(LocalDateTime.now()).build());
        review8.addStore(store4);

    }
    @Test
    @DisplayName("평균리뷰점수/내림차순으로 잘 정렬이 되는지 확인")
    void whenFindTopNByScore_ShouldReturnTopNStoresByScore() {
        setDetail();
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
        setDetail();
        // When
        List<TopStoreResponse> topStores = storeRepository.findTopNByType(3, StoreSearchType.MOST_REVIEWED.name(), "desc");
        // Then
        assertThat(topStores)
                .isNotEmpty()
                .noneMatch(response -> response.getStoreName().equals(store4.getName()))
                .allMatch(response -> !response.getStoreName().isEmpty());
    }


    @Test
    @DisplayName("좋아요수/내림차순으로 결과가 잘 나오는지 확인한다")
    void whenFindTopNByLikeCount_ShouldReturnFirstIsStore1() {
        //given
        Consumer consumer1 = consumerRepository.save(new Consumer("user1","dasd1@naver.com,",passwordEncoder.encode("sadad@123"), "name1", AuthenticationType.CONSUMER, true));
        likeRepository.save(new Likes(store1, consumer1));
        setDetail();
        // When

        List<TopStoreResponse> topStores = storeRepository.findTopNByType(3, StoreSearchType.MOST_LIKES_COUNT.name(), "desc");
        // Then
        assertThat(topStores.get(0).getStoreName()).isEqualTo(store1.getName());
        assertThat(topStores.get(0).getLikeCount()).isEqualTo(1);

    }

    @Test
    @DisplayName("가게를 키워드, 지역, 카테고리로 검색하면 그에 해당하는 결과를 페이징을 사용해서 반환해준다")
    void WhenSearchStoresWithKeywordLocationCategory_ThenReturnStore1() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, StoreSearchType.MOST_REVIEWED.name());
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



        String keyword = keyword1.getName();
        String location = "전주시";
        String category = store1.getCategory().getName();
        setDetail();
        // when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, category, keyword, location, "");

        // Then
        assertThat(topStores).isNotEmpty();
        assertThat(topStores.getContent().stream()
                .filter(response -> response.getStoreName().equals(store1.getName()))
                .allMatch(response ->
                        response.getAddress().equals(store1.getAddress()) &&
                                response.getAvgScore().equals(4.2)
                && !response.getStoreImageUrl().isEmpty()
                && !response.getAddress().isEmpty()
                && !response.getKeywordList().isEmpty()));
    }

    @Test
    @DisplayName("가게를 키워드, 지역, 카테고리, 가게이름으로 검색하면 그에 해당하는 결과를 반환해준다")
    void WhenSearchStoresWithAllKeyword_ThenReturnStore1() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, StoreSearchType.MOST_REVIEWED.name());
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

        String keyword = keyword1.getName();
        String location = "전주시";
        String category = store1.getCategory().getName();

        // when
        setDetail();
        Page<StoreResponseWithKeyword> topStores1 = storeRepository.findStoresByCondition(pageable, category, keyword, location, "1");
        Page<StoreResponseWithKeyword> topStores2 = storeRepository.findStoresByCondition(pageable, category, keyword, location, "1");

        // Then
        assertThat(topStores2.getSize()==1);
        assertThat(topStores1).isNotEmpty();
        assertThat(topStores1.getContent().stream()
                .filter(response -> response.getStoreName().equals(store1.getName()))
                .allMatch(response ->
                        response.getAddress().equals(store1.getAddress())
                                && response.getAvgScore().equals(4.2)
                                && !response.getStoreImageUrl().isEmpty()
                                && !response.getAddress().isEmpty()
                                && !response.getKeywordList().isEmpty())
        );
    }


    @Test
    @DisplayName("가게를 지역, 카테고리로만 검색하면 키워드는 필터링에서 제외하고 결과를 반환해준다")
    void WhenSearchStoresWithLocationCategoryExcludeKeyword_ThenReturnStore2WithAllKeywords() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, StoreSearchType.MOST_REVIEWED.name());
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

        String emptyKeyword = "";
        String location = "서초구";
        String category = category2.getName();

        // when
        setDetail();
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, category, emptyKeyword, location, "");

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
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, StoreSearchType.MOST_REVIEWED.name());
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

        String keyword = keyword3.getName();
        String emptyLocation = "";
        String category = category1.getName();
        setDetail();
        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, category,keyword,emptyLocation, "");

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
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, StoreSearchType.MOST_REVIEWED.name());
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

        String keyword = keyword3.getName();
        String location = "전주시";
        String emptyCategory = "";

        setDetail();
        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,keyword,location, "");

        // Then
        assertThat(topStores).isNotEmpty();
        assertThat(topStores.getContent().get(0).getStoreName()).isEqualTo(store1.getName());
        assertThat(topStores.getContent().get(0).getAddress()).isEqualTo(store1.getAddress());
        assertThat(topStores.getContent().get(0).getStoreImageUrl()).isEqualTo(image1.getUrl());
        assertThat(topStores.getContent().get(0).getAvgScore()).isEqualTo(4.2);

    }


    @Test
    @DisplayName("가게를 모든 키워드를 사용해서 검색해본다 결과는 하나가 나와야한다")
    void WhenSearchAllKeyword_ThenReturnStore1() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, StoreSearchType.MOST_REVIEWED.name());
        Keyword keyword1 = keywordRepository.save(new Keyword("감성 있는"));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword1));

        Category category1 = categoryRepository.save(new Category("양식"));
        store1.setCategory(category1);

        Image image1 = imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje1/(store1)"));
        imageRepository.save(new Image(store1, "http:s3.sae2/kjhkje3/(store1)"));

        String keyword = keyword1.getName();
        String location = "전주시";
        String category = category1.getName();
        String storeName = store1.getName();
        setDetail();
        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, category,keyword,location, storeName);

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
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, StoreSearchType.MOST_REVIEWED.name());

        Review review1 = reviewRepository.save(Review.builder().content("맛잇어요").score(4.2).createdDate(LocalDateTime.now()).build());
        Review review2 = reviewRepository.save(Review.builder().content("위생이안좋군요").score(2.2).createdDate(LocalDateTime.now()).build());
        review1.addStore(store1);
        review2.addStore(store1);

        String keyword = "";
        String location = "";
        String emptyCategory = "";
        setDetail();
        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,keyword,location, "");

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
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, StoreSearchType.MOST_REVIEWED.name());

        Review review1 = reviewRepository.save(Review.builder().content("맛잇어요").score(4.2).createdDate(LocalDateTime.now()).build());
        Review review2 = reviewRepository.save(Review.builder().content("위생이안좋군요").score(2.2).createdDate(LocalDateTime.now()).build());
        review1.addStore(store1);
        review2.addStore(store1);

        String keyword = "";
        String location = "";
        String emptyCategory = "";
        setDetail();
        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,keyword,location, "");

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
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, StoreSearchType.TOP_RATED.name());

        String emptyKeyword = "";
        String emptyLocation = "";
        String emptyCategory = "";
        setDetail();
        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,emptyKeyword,emptyLocation,"");

        // Then
        for (int i = 1; i < topStores.getContent().size(); i++) {
            assertThat(topStores.getContent().get(i).getAvgScore())
                    .isLessThanOrEqualTo(
                            topStores.getContent().get(i - 1).getAvgScore()
                    );
        }

    }

    @Test
    @DisplayName("가게를 키워드,지역,카테고리 필터링 제외시키고 결과리스트의 정렬됨만을 확인 - avgScore, 오름차순")
    void whenSortingParametersPassed_thenFindStoresByAvgScoreASCSortsResults() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, StoreSearchType.TOP_RATED.name());

        String emptyKeyword = "";
        String emptyLocation = "";
        String emptyCategory = "";
        setDetail();

        //when
        Page<StoreResponseWithKeyword> topStores = storeRepository.findStoresByCondition(pageable, emptyCategory,emptyKeyword,emptyLocation,"");

        // Then
        for (int i = 1; i < topStores.getContent().size(); i++) {
            assertThat(
                    topStores.getContent().get(i).getAvgScore())
                    .isGreaterThanOrEqualTo(
                            topStores.getContent().get(i - 1).getAvgScore()
                    );
        }
    }
    @Test
    @DisplayName("내가 좋아요 누른 가게에 대해 올바르게 결과가 나오는지 확인")
    void whenFindMyLikesStore_ThenSuccess() {
        // given
        Pageable pageable = PageRequest.of(0, 4);
        Member member = createLiker();
        createLike(member);
        createImage();
        createKeyword();
        setDetail();


        Page<MyLikeStoreResponse> myLikeStore = storeRepository.findMyLikeStore(member.getMemberId(), pageable);

        Assertions.assertEquals(myLikeStore.getTotalElements(),3);
        Assertions.assertEquals(myLikeStore.getTotalPages(),1);
        Assertions.assertTrue(myLikeStore.stream().anyMatch(sk ->
                        sk.getStoreName().equals(store1.getName())
                        && !sk.getStoreImageUrl().isEmpty()
                        && sk.getKeywordList().size()!=0
                        && sk.getPhone().equals(store1.getPhone())
                        && sk.getAddress().equals(store1.getAddress())));
        Assertions.assertTrue(myLikeStore.stream().anyMatch(sk ->
                         sk.getStoreName().equals(store2.getName())
                                 && !sk.getStoreImageUrl().isEmpty()
                                 && sk.getKeywordList().size()!=0
                        && sk.getPhone().equals(store2.getPhone())
                        && sk.getAddress().equals(store2.getAddress())));
        Assertions.assertTrue(myLikeStore.stream().anyMatch(sk ->
                sk.getStoreName().equals(store3.getName())
                        && !sk.getStoreImageUrl().isEmpty()
                        && sk.getKeywordList().size()!=0
                        && sk.getPhone().equals(store3.getPhone())
                        && sk.getAddress().equals(store3.getAddress())));
    }

    @Test
    @DisplayName("가게 정보 불러올때 요구한 모든 필드에 값이 들어있어야한다")
    public void whenFindDefaultActiveStoreInfo_ThenSuccess(){
        //given
        Member member = createLiker();
        createLike(member);
        createImage();
        createKeyword();
        Category category1 = categoryRepository.save(new Category("양식"));
        store1.setCategory(category1);
        setDetail();
        //when
        StoreDefaultResponse response = storeRepository.findDefaultActiveStoreInfo(store1.getStoreId()).get();
        //then
        Assertions.assertEquals(response.getLikeCount(),1);
        Assertions.assertEquals(response.getStoreName(),store1.getName());
        Assertions.assertEquals(response.getAddress(),store1.getAddress());
        Assertions.assertEquals(response.getKeywordList().size(),2);
        Assertions.assertEquals(response.getPhone(), store1.getPhone());
        Assertions.assertEquals(response.getCategoryName(), category1.getName());
    }
    private Member createLiker(){
        return memberRepository.save(CONSUMER);
    }
    private void createLike(Member member){
        likeRepository.save(new Likes(store1, member));
        likeRepository.save(new Likes(store2, member));
        likeRepository.save(new Likes(store3, member));
    }
    private void createImage(){
        imageRepository.save(new Image(store1, "url1"));
        imageRepository.save(new Image(store1, "url123"));
        imageRepository.save(new Image(store2, "url2"));
        imageRepository.save(new Image(store3, "url3"));
    }
    private void createKeyword(){
        Keyword keyword1 = keywordRepository.save(new Keyword("맛있는"));
        Keyword keyword2 = keywordRepository.save(new Keyword("분위기좋은"));
        Keyword keyword3 = keywordRepository.save(new Keyword("아이들과 오기 좋은"));
        Keyword keyword4 = keywordRepository.save(new Keyword("청결한"));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword1));
        storeKeywordRepository.save(new StoreKeyword(store2, keyword2));
        storeKeywordRepository.save(new StoreKeyword(store3, keyword3));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword4));
    }
    private void setDetail(){
            //모든 store의 like수, 리뷰 수, 리뷰 평점을 계산해서 집어 넣는다
            List<Store> storeList = storeRepository.findAll();
            List<Long> storeIdList = storeList.stream()
                    .map(Store::getStoreId)
                    .collect(Collectors.toList());

            Map<Long, Long> likesCountMap = likeRepository.countLikesByStoreIds(storeIdList);
            Map<Long, Long> reviewsCountMap = reviewRepository.countReviewsByStoreIds(storeIdList);
            Map<Long, Double> averageReviewScoreMap = reviewRepository.averageScoresByStoreIds(storeIdList);

            for (Store store : storeList) {
                Long storeId = store.getStoreId();
                Long likeCount = likesCountMap.getOrDefault(storeId, 0L);
                Long reviewCount = reviewsCountMap.getOrDefault(storeId, 0L);
                Double avgReviewScore = averageReviewScoreMap.getOrDefault(storeId, 0.0);

                store.setDetail(likeCount, reviewCount, avgReviewScore);
            }
    }
}
