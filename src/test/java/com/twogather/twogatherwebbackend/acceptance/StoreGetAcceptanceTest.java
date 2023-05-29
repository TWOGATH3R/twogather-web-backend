package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.TestUtil;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.StoreType;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import com.twogather.twogatherwebbackend.repository.*;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.twogather.twogatherwebbackend.TestConstants.passwordEncoded;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StoreGetAcceptanceTest {
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
    private EntityManager em;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private ConsumerRepository consumerRepository;

    private Store store1;
    private Store store2;
    private Store store3;
    private Store store4;
    static final int STORE_ENTITY_SIZE = 4;
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
        System.out.println("===================");
    }


    @Test
    @DisplayName("키워드, 카테고리, 지역으로 가게 검색하기")
    public void WhenSearchByKeywordCategoryAndLocation_ThenSuccess() throws Exception {
        //given
        Keyword keyword1 = keywordRepository.save(new Keyword("분위기가 좋은"));
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

        Category category1 = categoryRepository.save(new Category("앵식"));
        Category category2 = categoryRepository.save(new Category("중식"));
        store1.setCategory(category1);
        store2.setCategory(category2);

        Category temp1 = store1.getCategory();
        List<StoreKeyword> list = storeKeywordRepository.findByStoreStoreId(store1.getStoreId());
        List<Image> imageList = store1.getStoreImageList();

        em.flush();
        em.clear();
        //when
        mockMvc.perform(get("/api/stores/search")
                        .param("category", category1.getName())
                        .param("search", keyword1.getName())
                        .param("location", "전주시")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "reviewsCount,desc")
                        .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data[0].storeName").value(store1.getName()))
                .andExpect(jsonPath("$.data[0].address").value(store1.getAddress()))
                .andExpect(jsonPath("$.data[0].avgScore").value(3.2))
                .andExpect(jsonPath("$.data[0].storeImageUrl").exists())
                .andExpect(jsonPath("$.data[0].keywordList", hasSize(3)))
                .andReturn();

        //then
    }

    @Test
    @DisplayName("리뷰3은 평점이 가장 적기때문에 결과로 반환된 리스트들 중에서는 없어야한다")
    void WhenFindTopNByTopRated_ThenReturnValueExcludeStore3() throws Exception {
        StoreType type = StoreType.TOP_RATED;
        int count = 3;
        // When
        em.flush();
        em.clear();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/stores/top/{type}/{count}", type, count))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Then
        Response<List<TopStoreResponse>> response = TestUtil.convert(result, new TypeReference<Response<List<TopStoreResponse>>>(){});
        List<TopStoreResponse> topStores = response.getData();

        //store3는 평점 가장 적다
        for (TopStoreResponse item: topStores){
            assertThat(item.getStoreName()).isNotEqualTo(store3.getName());
            assertThat(item.getStoreName()).isNotBlank();
            System.out.println(item.getStoreName());
        }
    }

    @Test
    @DisplayName("리뷰4는 리뷰가 가장적기때문에 결과로 반환된 값중에 리뷰4는 없어야한다")
    void WhenFindTopNByReviewCount_ThenReturnValueExcludeStore4() throws Exception {
        StoreType type = StoreType.MOST_REVIEWED;
        int count = 3;
        em.flush();
        em.clear();
        // When
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/stores/top/{type}/{count}", type, count))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Then
        Response<List<TopStoreResponse>> response = TestUtil.convert(result, new TypeReference<Response<List<TopStoreResponse>>>(){});
        List<TopStoreResponse> topStores = response.getData();

        //store3는 평점 가장 적다
        for (TopStoreResponse item: topStores){
            assertThat(item.getStoreName()).isNotEqualTo(store4.getName());
            assertThat(item.getStoreName()).isNotBlank();
            System.out.println(item.getStoreName());
        }
    }


    @Test
    @DisplayName("존재하는 데이터 보다 많은 양을 요청했을땐 존재하는 데이터만 보여준다")
    void WhenFindTopNByReviewCountMoreThenCurrentDBData_ThenReturnExistData() throws Exception {
        StoreType type = StoreType.MOST_REVIEWED;
        int count = 10;
        // When
        em.flush();
        em.clear();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/stores/top/{type}/{count}", type, count)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Then
        Response<List<TopStoreResponse>> response = TestUtil.convert(result, new TypeReference<Response<List<TopStoreResponse>>>(){});
        List<TopStoreResponse> topStores = response.getData();

        assertThat(topStores.size()).isEqualTo(STORE_ENTITY_SIZE);
    }


    @Test
    @DisplayName("get: 가게 기본 정보 조회")
    @Transactional(readOnly = true)
    void WhenFindStoreInfo_ThenReturnStoreInfo() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/api/stores/{storeId}", store1.getStoreId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.storeName").value(store1.getName()))
                .andExpect(jsonPath("$.data.address").value(store1.getAddress()))
                .andExpect(jsonPath("$.data.phone").value(store1.getPhone()))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("likes 개수대로 정렬이 잘되는지 확인")
    void WhenFindTopNByMostLikesCount_ThenReturnSortedValues() throws Exception {
        StoreType type = StoreType.MOST_LIKES_COUNT;
        int count = 4;
        Consumer consumer1 = consumerRepository.save(new Consumer("user1","dasd1@naver.com,",passwordEncoded.encode("sadad@123"), "name1", AuthenticationType.CONSUMER, true));
        Consumer consumer2= consumerRepository.save(new Consumer("user2","das1d2@naver.com,",passwordEncoded.encode("sadad@123"), "name1", AuthenticationType.CONSUMER, true));
        Consumer consumer3 = consumerRepository.save(new Consumer("user3","dasd3@naver.com,",passwordEncoded.encode("sadad@123"), "name1", AuthenticationType.CONSUMER, true));
        Consumer consumer4 = consumerRepository.save(new Consumer("user4","dasd4@naver.com,",passwordEncoded.encode("sadad@123"), "name1", AuthenticationType.CONSUMER, true));

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
        // When
        em.flush();
        em.clear();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/stores/top/{type}/{count}", type, count))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Then
        Response<List<TopStoreResponse>> response = TestUtil.convert(result, new TypeReference<Response<List<TopStoreResponse>>>(){});
        List<TopStoreResponse> topStores = response.getData();

        assertThat(topStores.get(0).getStoreName()).isEqualTo(store4.getName());
        assertThat(topStores.get(1).getStoreName()).isEqualTo(store2.getName());
        assertThat(topStores.get(2).getStoreName()).isEqualTo(store1.getName());
        assertThat(topStores.get(3).getStoreName()).isEqualTo(store3.getName());
    }


}
