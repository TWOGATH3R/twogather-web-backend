package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.common.PagedResponse;
import com.twogather.twogatherwebbackend.dto.common.Response;
import com.twogather.twogatherwebbackend.dto.store.StoreSearchType;
import com.twogather.twogatherwebbackend.dto.store.MyStoreResponse;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import com.twogather.twogatherwebbackend.repository.*;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.util.TestUtil.convert;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class StoreGetAcceptanceTest extends AcceptanceTest{
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StoreKeywordRepository storeKeywordRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private ConsumerRepository consumerRepository;
    @Autowired
    private StoreOwnerRepository ownerRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Store store1;
    private Store store2;
    private Store store3;
    private Store store4;
    private static final String CATEGORY_NAME_1 = "분식";
    private static final String KEYWORD_NAME_1 = "맛있는";
    private static final int STORE_ENTITY_SIZE = 4;

    @BeforeEach
    public void setUp() {
        super.setUp();
        Category category1 = categoryRepository.save(new Category(CATEGORY_NAME_1));
        Category category2 = categoryRepository.save(new Category("중식"));

        registerOwner();
        StoreOwner owner = ownerRepository.findActiveMemberById(loginMemberId).get();

        store1 = storeRepository.save(Store.builder().category(category1).name("가게1").address("전주시 어쩌고 어저고").phone("063-231-4444").status(StoreStatus.APPROVED).owner(owner).build());
        store2 = storeRepository.save(Store.builder().category(category1).name("가게2").address("서울시 어쩌고 어저고").phone("010-1234-1234").status(StoreStatus.APPROVED).owner(owner).build());
        store3 = storeRepository.save(Store.builder().category(category1).name("가게3").address("대전광역시 어쩌고 어쩌고").phone("02-232-2222").status(StoreStatus.APPROVED).owner(owner).build());
        store4 = storeRepository.save(Store.builder().category(category2).name("가게4").address("서울시 어쩌고 어쩌고").phone("063-231-4444").status(StoreStatus.APPROVED).owner(owner).build());

        Review review1 = Review.builder().content("맛잇어요").score(4.2).createdDate(LocalDateTime.now()).build();
        Review review2 = Review.builder().content("위생이안좋군요").score(2.2).createdDate(LocalDateTime.now()).build();
        review1.addStore(store1);
        review2.addStore(store1);
        
        Review review3 = Review.builder().content("분위기가좋아요").score(4.4).createdDate(LocalDateTime.now()).build();
        Review review4 =Review.builder().content("아이들과 오기 좋네요").score(3.2).createdDate(LocalDateTime.now()).build();
        review3.addStore(store2);
        review4.addStore(store2);
        
        Review review5 = Review.builder().content("아이들과 오기 좋네요").score(1.2).createdDate(LocalDateTime.now()).build();
        Review review6 = Review.builder().content("분위기가좋아요").score(1.4).createdDate(LocalDateTime.now()).build();
        Review review7 = Review.builder().content("아이들과 오기 좋네요").score(1.2).createdDate(LocalDateTime.now()).build();
        review5.addStore(store3);
        review6.addStore(store3);
        review7.addStore(store3);
        
        Review review8 =  Review.builder().content("아이들과 오기 좋네요").score(4.2).createdDate(LocalDateTime.now()).build();
        review8.addStore(store4);

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);
        reviewRepository.save(review4);
        reviewRepository.save(review5);
        reviewRepository.save(review6);
        reviewRepository.save(review7);
        reviewRepository.save(review8);

    }

    @Test
    @DisplayName("키워드, 카테고리, 지역으로 가게 검색하기")
    public void WhenSearchByKeywordCategoryAndLocation_ThenSuccess() {
        //given
        settingKeywordCategoryImage();

        //when
        given().param("category", CATEGORY_NAME_1)
                .param("search", KEYWORD_NAME_1)
                .param("location", "전주시")
                .param("page", "0")
                .param("size", "2")
                .param("sort", StoreSearchType.MOST_REVIEWED.name()+",desc")
                .get("/api/stores/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .body("data[0].storeName", equalTo(store1.getName()))
                .body("data[0].address", equalTo(store1.getAddress()))
                .body("data[0].avgScore", equalTo(3.2F))
                .body("data[0].storeImageUrl", notNullValue())
                .body("data[0].keywordList.size()", equalTo(3));

    }

    @Test
    @DisplayName("모든 조건이 없을때 존재하는 데이터가 모두 나와야한다")
    public void WhenSearchByAllNull_ThenReturnAllData() {
        //given
        settingKeywordCategoryImage();

        //when
        given().param("category", "")
                .param("search", "")
                .param("location", "")
                .param("page", "0")
                .param("size", "2")
                .param("sort", StoreSearchType.MOST_REVIEWED.name()+",desc")
                .get("/api/stores/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .body("currentPage", equalTo(0))
                .body("totalPages", equalTo(2))
                .body("totalElements", equalTo(4))
                .body("pageSize", equalTo(2))
                .body("isFirst", equalTo(true))
                .body("isLast", equalTo(false));


    }

    @Test
    @DisplayName("리뷰3은 평점이 가장 적기때문에 결과로 반환된 리스트들 중에서는 없어야한다")
    void WhenFindTopNByTopRated_ThenReturnValueExcludeStore3() {
        StoreSearchType type = StoreSearchType.TOP_RATED;
        int count = 3;
        // When
        Response result = given().contentType(ContentType.JSON)
                .get("/api/stores/top/{type}/{count}", type, count)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Response.class);

        // Then
        List<TopStoreResponse> response = convert(result, new TypeReference<List<TopStoreResponse>>() {});

        //store3는 평점 가장 적다
        for (TopStoreResponse item: response){
            assertThat(item.getStoreName()).isNotEqualTo(store3.getName());
            assertThat(item.getStoreName()).isNotBlank();
            System.out.println(item.getStoreName());
        }
    }

    @Test
    @DisplayName("리뷰4는 리뷰가 가장적기때문에 결과로 반환된 값중에 리뷰4는 없어야한다")
    void WhenFindTopNByReviewCount_ThenReturnValueExcludeStore4()  {
        StoreSearchType type = StoreSearchType.MOST_REVIEWED;
        int count = 3;
        // When
        Response result = given().contentType(ContentType.JSON)
                .get("/api/stores/top/{type}/{count}", type, count)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Response.class);

        // Then
        List<TopStoreResponse> response = convert(result, new TypeReference<List<TopStoreResponse>>() {});

        //store3는 평점 가장 적다
        for (TopStoreResponse item: response){
            assertThat(item.getStoreName()).isNotEqualTo(store4.getName());
            assertThat(item.getStoreName()).isNotBlank();
            System.out.println(item.getStoreName());
        }
    }


    @Test
    @DisplayName("존재하는 데이터 보다 많은 양을 요청했을땐 존재하는 데이터만 보여준다")
    void WhenFindTopNByReviewCountMoreThenCurrentDBData_ThenReturnExistData()  {
        StoreSearchType type = StoreSearchType.MOST_REVIEWED;
        int count = 10;
        // When
        Response result = given().contentType(ContentType.JSON)
                .get("/api/stores/top/{type}/{count}", type, count)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Response.class);


        // Then
        List<TopStoreResponse> response = convert(result, new TypeReference<List<TopStoreResponse>>() {});

        assertThat(response.size()).isEqualTo(STORE_ENTITY_SIZE);
    }


    @Test
    @DisplayName("get: 가게 기본 정보 조회")
    public void WhenFindStoreInfo_ThenReturnStoreInfo() {
        // When
        given().get("/api/stores/" + store1.getStoreId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.storeName", equalTo(store1.getName()))
                .body("data.address", equalTo(store1.getAddress()))
                .body("data.phone", equalTo(store1.getPhone()));

    }


    @Test
    @DisplayName("likes 개수대로 정렬이 잘되는지 확인")
    void WhenFindTopNByMostLikesCount_ThenReturnSortedValues() {
        StoreSearchType type = StoreSearchType.MOST_LIKES_COUNT;
        int count = 4;
        addLike();
        // When

        Response result = given().contentType(ContentType.JSON)
                .get("/api/stores/top/{type}/{count}", type, count)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Response.class);


        // Then
        List<TopStoreResponse> response = convert(result, new TypeReference<List<TopStoreResponse>>() {});

        assertThat(response.get(0).getStoreName()).isEqualTo(store4.getName());
        assertThat(response.get(1).getStoreName()).isEqualTo(store2.getName());
        assertThat(response.get(2).getStoreName()).isEqualTo(store1.getName());
        assertThat(response.get(3).getStoreName()).isEqualTo(store3.getName());
    }

    @Test
    @DisplayName("findMyStore - 페이징이 잘 적용되는지 확인해본다")
    public void WhenFindMyStoreWithPaging_ThenReturnExactValue() {
        //given
        String url = "/api/my/stores/";
        //when
        //then
        given()
                .param("ownerId", loginMemberId)
                .param("page", "0")
                .param("size", "2")
                .param("sort", "MOST_REVIEWED,desc")
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getRefreshToken())
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getAccessToken())
                .get(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("currentPage", equalTo(0))
                .body("totalPages", equalTo(2))
                .body("totalElements", equalTo(4))
                .body("pageSize", equalTo(2))
                .body("isFirst", equalTo(true))
                .body("isLast", equalTo(false));

    }


    @Test
    @DisplayName("나의 가게 조회")
    public void whenFindMyStore_ThenSuccess(){
        //given
        saveStore();

        //when
        List<MyStoreResponse> list = returnMyStoreList();

        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게10") &&
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게11") &&
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게12") &&
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게13") &&
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게14") &&
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("안승인된 가게") &&
                        e.getStatus().equals(StoreStatus.DENIED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));

    }

    private List<MyStoreResponse> returnMyStoreList(){
        Long ownerId = ownerRepository.findByUsername(OWNER_USERNAME).get().getMemberId();
        String url = "/api/my/stores";
        List<MyStoreResponse> result = convert(given()
                        .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getRefreshToken())
                        .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getAccessToken())
                        .param("ownerId", ownerId)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .get(url)
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.OK.value()).extract().as(PagedResponse.class),
                new TypeReference<>() {});
        return result;
    }

    private void addLike(){
        Consumer consumer1 = consumerRepository.save(new Consumer("user1","dasd1@naver.com,",passwordEncoder.encode("sadad@123"), "name1", AuthenticationType.CONSUMER, true));
        Consumer consumer2= consumerRepository.save(new Consumer("user2","das1d2@naver.com,",passwordEncoder.encode("sadad@123"), "name2", AuthenticationType.CONSUMER, true));
        Consumer consumer3 = consumerRepository.save(new Consumer("user3","dasd3@naver.com,",passwordEncoder.encode("sadad@123"), "name3", AuthenticationType.CONSUMER, true));
        Consumer consumer4 = consumerRepository.save(new Consumer("user4","dasd4@naver.com,",passwordEncoder.encode("sadad@123"), "name4", AuthenticationType.CONSUMER, true));

        likeRepository.save(new Likes(store4, consumer1));
        likeRepository.save(new Likes(store4, consumer2));
        likeRepository.save(new Likes(store4, consumer3));
        likeRepository.save(new Likes(store4, consumer4));

        likeRepository.save(new Likes(store2, consumer1));
        likeRepository.save(new Likes(store2, consumer2));
        likeRepository.save(new Likes(store2, consumer3));

        likeRepository.save(new Likes(store1, consumer1));
        likeRepository.save(new Likes(store1, consumer2));

        likeRepository.save(new Likes(store3, consumer1));
    }
    private void saveStore(){
        StoreOwner owner = ownerRepository.findByUsername(OWNER_USERNAME).get();
        for(int i=10;i<15;i++){
            Store store = Store.builder()
                    .owner(owner)
                    .name("가게" + i)
                    .address(STORE_ADDRESS)
                    .phone(STORE_PHONE)
                    .status(StoreStatus.APPROVED).build();
            storeRepository.save(store);
        }
        Store deniedStore = Store.builder()
                .owner(owner)
                .name("안승인된 가게")
                .address(STORE_ADDRESS)
                .phone(STORE_PHONE)
                .status(StoreStatus.DENIED).build();
        storeRepository.save(deniedStore);
    }
    private void settingKeywordCategoryImage(){
        Keyword keyword1 = keywordRepository.save(new Keyword(KEYWORD_NAME_1));
        Keyword keyword2 = keywordRepository.save(new Keyword("청결한"));
        Keyword keyword3 = keywordRepository.save(new Keyword("가족들과 시간보내기 좋은"));
        Keyword keyword4 = keywordRepository.save(new Keyword("저렴한"));
        Keyword keyword5 = keywordRepository.save(new Keyword("친절한"));

        storeKeywordRepository.save(new StoreKeyword(store1, keyword1));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword2));
        storeKeywordRepository.save(new StoreKeyword(store1, keyword3));
        storeKeywordRepository.save(new StoreKeyword(store2, keyword3));
        storeKeywordRepository.save(new StoreKeyword(store2, keyword4));
        storeKeywordRepository.save(new StoreKeyword(store2, keyword5));

        Image image1 = imageRepository.save(new Image(store1,"url1"));
        Image image2 = imageRepository.save(new Image(store1,"url2"));
        Image image3 = imageRepository.save(new Image(store2,"url3"));


    }

}
