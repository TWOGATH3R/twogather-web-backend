package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.TestUtil;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreApprovalStatus;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.StoreType;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.twogather.twogatherwebbackend.TestConstants.OWNER_SAVE_REQUEST2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class StoreGetAcceptanceTest {
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
    static final int STORE_ENTITY_SIZE = 4;
    @BeforeEach
    void setUp() {
        store1 = storeRepository.save(new Store(null,"가게1","전주시 어쩌고어쩌고","063-231-4444", StoreApprovalStatus.APPROVED,null));
        store2 = storeRepository.save(new Store(null,"가게2", "서울시 어쩌고 어저고", "010-1234-1234",StoreApprovalStatus.APPROVED,null));
        store3 = storeRepository.save(new Store(null,"가게3", "대전광역시 어쩌고 어쩌고", "02-232-2222",StoreApprovalStatus.APPROVED,null));
        store4 = storeRepository.save(new Store(null,"가게4", "서울시 어쩌고 어쩌고", "02-232-2522", StoreApprovalStatus.APPROVED,null));

        Review review1 = reviewRepository.save(new Review(store1, null, "맛잇어요", 4.2, LocalDate.of(2020,02,02)));
        Review review2 = reviewRepository.save(new Review(store1, null, "위생이안좋군요", 2.2, LocalDate.of(2022,04,02)));

        Review review3 = reviewRepository.save(new Review(store2, null, "깨끗하넨요", 4.4, LocalDate.of(2021,01,12)));
        Review review4 = reviewRepository.save(new Review(store2, null, "깨끗하넨요", 3.2, LocalDate.of(2019,01,12)));

        Review review5 = reviewRepository.save(new Review(store3, null, "깨끗하넨요", 1.2, LocalDate.of(2019,01,12)));
        Review review6 = reviewRepository.save(new Review(store3, null, "분위기가좋아요", 1.4, LocalDate.of(2021,01,12)));
        Review review7 = reviewRepository.save(new Review(store3, null, "깨끗하넨요", 1.2, LocalDate.of(2019,01,12)));

        Review review8 = reviewRepository.save(new Review(store4, null, "아이들과 오기 좋네요", 4.2, LocalDate.of(2019,01,12)));

        //store4: 리뷰가 가장적다
        //store3: 평점이 가장 적다
        em.flush();
        System.out.println("===================");
    }


    @Test
    @DisplayName("리뷰3은 평점이 가장 적기때문에 결과로 반환된 리스트들 중에서는 없어야한다")
    @Transactional
    void WhenFindTopNByTopRated_ThenReturnValueExcludeStore3() throws Exception {
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

        //store3는 평점 가장 적다
        for (TopStoreResponse item: topStores){
            assertThat(item.getStoreName()).isNotEqualTo(store3.getName());
            assertThat(item.getStoreName()).isNotBlank();
            System.out.println(item.getStoreName());
        }
    }

    @Test
    @DisplayName("리뷰4는 리뷰가 가장적기때문에 결과로 반환된 값중에 리뷰4는 없어야한다")
    @Transactional
    void WhenFindTopNByReviewCount_ThenReturnValueExcludeStore4() throws Exception {
        StoreType type = StoreType.MOST_REVIEWED;
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

        //store3는 평점 가장 적다
        for (TopStoreResponse item: topStores){
            assertThat(item.getStoreName()).isNotEqualTo(store4.getName());
            assertThat(item.getStoreName()).isNotBlank();
            System.out.println(item.getStoreName());
        }
    }

    @Test
    @DisplayName("존재하는 데이터 보다 많은 양을 요청했을땐 존재하는 데이터만 보여준다")
    @Transactional
    void WhenFindTopNByReviewCountMoreThenCurrentDBData_ThenReturnExistData() throws Exception {
        StoreType type = StoreType.MOST_REVIEWED;
        int count = 10;
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
    @DisplayName("가장 많이 언급된 키워드를 뽑는다")
    public void whenGetKeyword_ThenGenerateKeywordInRandomNReview() throws Exception {
        reviewRepository.save(new Review(store1, null,
                "나의 최애 화이트라구 파스타를 파는 곳.. 여기서 화이트라구를 처음 접해보고 반해서 다른 곳에서도 화이트라구를 먹어봤지만 그 어디에서도 여기 같은 맛을 찾을 수가 없었다는 슬픈 이야긔..ㅠㅠ 징쨔 여기만큼 화이트라구 맛있게 하는 데 없어요.. 화이트라구 맛있게 하는 데 아시는 분 추천 좀 해주세여.. 여기 예약 너무 빡세요ㅠㅠㅠㅠ \n" +
                "\n" +
                "얇은 감튀 좋아하는 저한테는 여기 감튀도 완전 취향저격스ㅎㅎㅎㅎ 별거 아닌데도 너무 맛있어서 꼭 시키게 되는 메뉴입니당 아그작아그작 씹어먹기 너무 조아용\n" +
                "\n" +
                "이번엔 새우리조또도 시켜봤는데 새우 향이 정말 진하게 나서 해산물 좋아하시는 분들이라면 무적권 좋아할 맛!! \n" +
                "\n" +
                "여기 분위기도 너무 좋고 음식도 너무 맛있는데 예약이 너무 빡세요ㅜㅜ 맘같아선 일주일에 한번씩 가서 모든 메뉴 다 먹어보고 싶은 그런 곳이에영 힝", 4.2, LocalDate.of(2020,02,02)));
        reviewRepository.save(new Review(store1, null, "정말 유명한곳이죠! 이곳은 꾸준히 인기있는 것 같아요:)\n" +
                "\n" +
                "트러플 화이트라구 파스타! 생면이라 면이 정말 탱탱하니 맛있어요. 잘 섞어먹으면 트러플향, 치즈향 등이 잘 어우러져서 너무 맛있어요! 역시 수준급의 파스타네요 ㅎㅎ 다만 간이 생각보다 더 짭짤한 편이라 의외로 호불호가 갈리더라구요! 저는 두 입까지는 극호, 그 이후부터는 살짝 호인걸로..ㅎ 고기도 넉넉히 들어있어서 은근 든든해요.", 2.2, LocalDate.of(2022,04,02)));
        reviewRepository.save(new Review(store2, null, "♤ 화이트 라구 따야린 (28,000₩)\n" +
                "+ 트러플 추가 (10,000₩)\n" +
                "트러플이 있고 없고의 차이가 좀 있긴 한데, 꼭 추가할 필요는 없을 것 같다. 트러플 없이도 충분히 환상적인 맛이다. 따야린 생면의 녹아내리는 듯한 식감과, 고소하고 짭쪼름한 화이트 라구 소스. 리뷰 쓰는 지금도 군침이 돈다. 물론 트러플이 있으면 금상첨화. 눈앞에서 갈아서 올려 주신다.\n" +
                "\n" +
                "♤ 우니 파스타 (29,000₩)\n" +
                "성게알과 생선이 주는 바다 향이 강하게 풍기는 파스타. 올리브 오일의 풍미도 좋고, 중간중간에 들어간 빵가루가 깨알같이 소스를 잡아주는 역할을 한다. 이 메뉴 역시 맛있었다. 파스타 라인업 중 두 개를 고른다면, 하나는 화이트 라구 고정에 나머지 하나는 이거 아니면 스파이시 크랩 리조토 중 그 날 당기는 것으로 고르면 될 것 같다.\n" +
                "--------\n" +
                "트러플을 눈 앞에서 갈아주거나, 파스타를 섞어 주는 퍼포먼스도 볼 만한 요소. 예약하기도 그렇게 어렵진 않고, 심지어 이 날은 예약도 안 하고 갔는데 마침 바 자리가 남아 있어 수월하게 먹을 수 있었다.\n" +
                "\n" +
                "어렵게 생면 파스타 바를 방문해서, 주류 필수 정책에 좋아하지도 않는 와인 고르느라 머리를 굴리는 것이, 여기의 화이트 라구와 비프 카르파치오보다 더 큰 만족감을 줄 수 있을까? 그럴 가능성은 별로 없을 것 같다.", 4.4, LocalDate.of(2021,01,12)));
        reviewRepository.save(new Review(store2, null, "바위, 페리지, 우오보, 시멘트 그 이전부터 당당히 생면의 매력을 빛내던 자가 있었으니 이름하여 오르조…!! 오르조야 말로 파스타집의 근본 중의 근본이 아니었나ㅎㅎ\n" +
                "\n" +
                "이날 시킨 전채요리는 카라멜라이즈드 된 호두와 피스타치오가 크런치하니 달달하고 맛있었던 비프 카르파치오. 사진 찍으라고 먼저 내어주신 후 촬영 후엔 먹기좋기 돌돌 감싸주신다 요놈도 아주 요물이다. 꼭 시켜먹어야하는 플레이트 중 하나.\n" +
                "\n" +
                "하지만 내 기준 이 집의 시그니처는 바로 화이트 라구 생면파스타 생면의 특성상 잘 불을 수 있다고 하셨지만, 뭐… 그 전에 이미 내 위장 속으로 직행해있으니 아주 터무니 없는 걱정이었고.. 따야린은 식감이 잘 살아있는게, 뚝뚝 아니고 톡톡 끊기는게 매력있었다!! (아주 다름) \n" +
                "와인이 너무나도 땡기는 맛이었으나 우리는 와인바에 (결국 두 군데나 더) 가야했기에 참았다… \n" +
                "아 그리고 트러플은 별도의 요금을 내고 추가를 해야하는데 그래야 매력이 배가 되기에 솔직히 필수다\n" +
                "\n" +
                "스파이시 크랩리조또는 궁금한 마음에 밸런스를 위해 시켜보았는데 살짝 미스테잌… 내 입맛엔 그저 덜 익은 쌀 같았달까 소스는 굉장히 매콤했다.", 3.2, LocalDate.of(2019,01,12)));
        reviewRepository.save(new Review(store3, null, "망고플레이트에서 추천받은 맛집을 방문했는데, 최악의 접객을 경험했습니다. 처음에 3명으로 인원수를 말했더니 일행이 도착하면 입장 가능하다는 말에 한 명이 더 도착하자 바로 입장할 수 있었습니다. 주문한 음식이 나온 후 한 명이 더 올 예정이라고 말했더니 이미 나온 음식을 도로 가져가며 저희 테이블 음식을 바로 다른 테이블로 옮기더군요. 곧 도착할 다른 한 명은 매장에 들어올 수 없다며..;; 이미 3명이 올거라고 말했으나 전체 일행이 도착해야만 입장 가능하다는 것을 미리 알려주지 않아 불편했습니다. 이미 음식이 나온 상황에서 매장을 나가라고 하는 서비스는 처음 봤습니다. \n" +
                "\n" +
                "음식 맛도 중요하지만, 고객 대응이 더 중요하다고 생각합니다. 이 식당은 기본적인 접객 서비스를 제공하지 않아 최악의 서비스를 경험했습니다.", 1.2, LocalDate.of(2019,01,12)));
        reviewRepository.save(new Review(store3, null, "분위기가좋아요", 1.4, LocalDate.of(2021,01,12)));
        reviewRepository.save(new Review(store3, null, "직원들도 친절해요 그리고 매장내 가 굉장히 청결해요", 1.2, LocalDate.of(2019,01,12)));
        reviewRepository.save(new Review(store4, null, "직원들도 친절해요", 4.2, LocalDate.of(2019,01,12)));
        reviewRepository.save(new Review(store1, null, "맛잇어요", 4.2, LocalDate.of(2020,02,02)));
        reviewRepository.save(new Review(store1, null, "위생이안좋군요", 2.2, LocalDate.of(2022,04,02)));

        reviewRepository.save(new Review(store2, null, "청결하구요", 3.2, LocalDate.of(2019,01,12)));
        reviewRepository.save(new Review(store3, null, "그리고 청결해요", 1.2, LocalDate.of(2019,01,12)));
        reviewRepository.save(new Review(store4, null, "청결해요 청결해요 청결해요", 4.2, LocalDate.of(2019,01,12)));

        em.flush();
        em.clear();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/stores/keyword"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

}
