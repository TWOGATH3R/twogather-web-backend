package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(StoreController.class)
public class StoreControllerTest extends ControllerTest{
    @MockBean
    private StoreService storeService;

    @Test
    public void update() throws Exception {
        //given
        when(storeService.update(anyLong(), any())).thenReturn(STORE_RESPONSE);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/stores/{storeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(STORE_UPDATE_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("store/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("가게주소").attributes(getStorePhoneFormat()),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("가게전화번호")
                        ),
                        responseFields(
                                fieldWithPath("data.storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("가게주소").attributes(getStorePhoneFormat()),
                                fieldWithPath("data.phone").type(JsonFieldType.STRING).description("가게전화번호")

                        )
                ));

    }

    @Test
    @DisplayName("가게 삭제")
    public void deleteStore() throws Exception {
        //given
        doNothing().when(storeService).delete(anyLong());
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/stores/{storeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 id")
                        )
                        )
                );
    }

    @Test
    @DisplayName("나의가게조회")
    public void getMyStoreInfo() throws Exception {
        //given
        when(storeService.getStoresByOwner(anyLong(), anyInt(), anyInt())).thenReturn(MY_STORES_RESPONSE_PAGE);
        //when
        //then
        mockMvc.perform(get("/api/stores/my?owner-id=1&limit=1&offset=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-my-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("owner-id").description("가게 주인의 ID"),
                                parameterWithName("limit").description("한 페이지에 조회할 가게 수"),
                                parameterWithName("offset").description("조회할 가게 목록의 시작 위치")
                        ),
                        responseFields(
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data[].storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data[].storePhone").type(JsonFieldType.STRING).description("가게 전화번호").attributes(getStorePhoneFormat()),
                                fieldWithPath("data[].requestDate").type(JsonFieldType.STRING).description("승인 요청한 날짜").attributes(getDateFormat()),
                                fieldWithPath("data[].storeImageUrl").type(JsonFieldType.STRING).description("가게 대표 이미지 URL"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data[].isApproved").type(JsonFieldType.BOOLEAN).description("관리자에 의해 가게가 승인됐는지의 여부"),
                                fieldWithPath("data[].reasonForRejection").type(JsonFieldType.STRING).description("가게가 승인되지 않은 이유"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 개수"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지인지 여부"),
                                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재페이지가 몇번인지")

                                )
                ));

    }

    @Test
    @DisplayName("첫 화면에서 보여줄 가게의 대략적인 정보 - detail")
    public void getStoreTopInfos() throws Exception {
        //given
        when(storeService.getStoresTop10(any())).thenReturn(STORES_TOP10_RESPONSE_LIST);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/stores/top/{type}", "topRatedStores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-list-default-detail",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("type").description("자세히 볼 페이지의 type").attributes(getStoreType())
                        ),
                        responseFields(
                                fieldWithPath("data[].storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data[].score").type(JsonFieldType.NUMBER).description("가게 평점"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data[].storeImageUrl").type(JsonFieldType.STRING).description("가게 대표 이미지 url")
                        )
                ));

    }

    @Test
    @DisplayName("첫 화면에서 보여줄 검색 키워드 목록")
    public void getKeyword() throws Exception {
        //given
        when(storeService.getKeyword()).thenReturn(KEYWORD_LIST);
        //when
        //then
        mockMvc.perform(get("/api/stores/keyword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-keyword",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("키워드 이름 리스트")

                        )
                ));

    }

    @Test
    @DisplayName("첫 화면에서 보여줄 가게의 대략적인 정보")
    public void getStoreTopPreviewInfos() throws Exception {
        //given
        when(storeService.getStoresTop10Preview()).thenReturn(STORES_TOP10_PREVIEW_RESPONSE);
        //when
        //then
        mockMvc.perform(get("/api/stores/top-preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-list-default",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("data.popularStores").type(JsonFieldType.ARRAY).description("가장 인기 있는 가게의 정보. 3개만 전송"),
                                fieldWithPath("data.topRatedStores").type(JsonFieldType.ARRAY).description("가장 평점이 높은 가게의 정보. 3개만 전송"),
                                fieldWithPath("data.popularStores[].storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data.popularStores[].score").type(JsonFieldType.NUMBER).description("가게 평점"),
                                fieldWithPath("data.popularStores[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data.popularStores[].storeImageUrl").type(JsonFieldType.STRING).description("가게 대표 이미지 url"),
                                fieldWithPath("data.topRatedStores[].storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data.topRatedStores[].score").type(JsonFieldType.NUMBER).description("가게 평점"),
                                fieldWithPath("data.topRatedStores[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data.topRatedStores[].storeImageUrl").type(JsonFieldType.STRING).description("가게 대표 이미지 url")

                        )
                ));

    }

    @Test
    @DisplayName("가게 여러건 조회")
    public void getStoreInfosMethod_WhenGetStoreInfos_ThenReturnStoreInfos() throws Exception {
        //given
        when(storeService.getStores(any(), any(), anyInt(), anyInt(), any(), any(), any())).thenReturn(STORES_RESPONSE_LIST);
        //when
        //then
        mockMvc.perform(get("/api/stores/search")
                        .param("category", "categoryName1")
                        .param("search", "keyword1")
                        .param("limit", "1")
                        .param("offset", "2")
                        .param("orderBy", "desc")
                        .param("order", "rating")
                        .param("location", "!#!@#!@#!")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-list-search",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("category").description("조회할 가게 카테고리 이름"),
                                parameterWithName("search").description("검색할 가게 이름 키워드"),
                                parameterWithName("limit").description("한 페이지에서 조회할 가게 수"),
                                parameterWithName("offset").description("조회할 가게 리스트에서의 시작 위치"),
                                parameterWithName("orderBy").description("가게 조회 결과를 정렬 기준 항목"),
                                parameterWithName("order").description("가게 조회 결과를 정렬 순서"),
                                parameterWithName("location").description("검색하기위한 지역정보(한글이 인코딩되어있을것임)")

                        ),
                        responseFields(
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data[].rating").type(JsonFieldType.NUMBER).description("가게 별점 정보").attributes(getRatingFormat()),
                                fieldWithPath("data[].keywordList").type(JsonFieldType.ARRAY).description("가게 관련 키워드"),
                                fieldWithPath("data[].storeImageUrl").type(JsonFieldType.STRING).description("가게 대표 사진 url")

                        )
                ));

    }


    @Test
    @DisplayName("가게 단건 조회")
    public void getStoreInfoMethod_WhenGetStoreInfo_ThenReturnStoreInfo() throws Exception {
        //given
        when(storeService.getStore(any())).thenReturn(STORE_RESPONSE);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/stores/{storeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-one",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("data.storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("가게주소").attributes(getStorePhoneFormat()),
                                fieldWithPath("data.phone").type(JsonFieldType.STRING).description("가게전화번호")
                        )
                ));

    }
    @Test
    @DisplayName("가게저장시에 요청한정보 + id 정보 반환")
    public void saveStoreMethod_WhenSaveStore_ThenReturnIdAndInfo() throws Exception {
        //given
        when(storeService.save(any())).thenReturn(STORE_RESPONSE);
        //when
        //then
        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(STORE_REQUEST))
                )
                .andExpect(status().isCreated())
                .andDo(document("store/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("가게주소").attributes(getStorePhoneFormat()),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("가게전화번호")
                      ),
                        responseFields(
                                fieldWithPath("data.storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("가게주소").attributes(getStorePhoneFormat()),
                                fieldWithPath("data.phone").type(JsonFieldType.STRING).description("가게전화번호")

                        )
                ));

    }
}
