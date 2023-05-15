package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.TestUtil;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.StoreType;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import com.twogather.twogatherwebbackend.repository.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.twogather.twogatherwebbackend.TestConstants.OWNER_SAVE_REQUEST2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class StoreTest {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Store store1;
    private Store store2;
    private Store store3;
    private Store store4;
    @BeforeEach
    void setUp() {
        store1 = storeRepository.save(new Store(null,"가게1","전주시 어쩌고어쩌고","063-231-4444",true,null));
        store2 = storeRepository.save(new Store(null,"가게2", "서울시 어쩌고 어저고", "010-1234-1234",true,null));
        store3 = storeRepository.save(new Store(null,"가게3", "대전광역시 어쩌고 어쩌고", "02-232-2222",true,null));
        store4 = storeRepository.save(new Store(null,"가게4", "서울시 어쩌고 어쩌고", "02-232-2522", true,null));

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
    void WhenFindTopNByScore_ThenReturnTopNStoresByScoreExcludeStore3() throws Exception {
        StoreType type = StoreType.TOP_RATED;
        int count = 3;
        // When
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/stores/top/{type}/{count}", type, count)
                        .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(OWNER_SAVE_REQUEST2)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

       // Then
        Response<List<TopStoreResponse>> response = TestUtil.convert(result, new TypeReference<Response<List<TopStoreResponse>>>(){});
        List<TopStoreResponse> topStores = response.getData();

        assertThat(topStores).isNotEmpty();

        //store3는 리뷰가 가장 적다
        for (TopStoreResponse item: topStores){
            assertThat(item.getStoreName()).isNotEqualTo(store3.getName());
            assertThat(item.getStoreName()).isNotBlank();
        }
    }

    @Test
    @DisplayName("findTopNByReviewCount should return the top N stores by review count")
    @Transactional
    void WhenFindTopNByReviewCount_ThenReturnTopNByReviewCountExcludeStore4() {
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
