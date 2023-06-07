package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.TestConstants;
import com.twogather.twogatherwebbackend.dto.StoreSearchType;
import com.twogather.twogatherwebbackend.dto.store.StoreResponseWithKeyword;
import com.twogather.twogatherwebbackend.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

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
    public void update_WhenStoreUpdate_ThenReturnStoreInfo() throws Exception {
        //given
        when(storeService.update(anyLong(), any())).thenReturn(STORE_SAVE_UPDATE_RESPONSE);
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
                                fieldWithPath("storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("가게전화번호").attributes(getStorePhoneFormat()),
                                fieldWithPath("businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        ),
                        responseFields(
                                fieldWithPath("data.storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data.storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data.phone").type(JsonFieldType.STRING).description("가게전화번호").attributes(getStorePhoneFormat())

                        )
                ));

    }

    @Test
    @DisplayName("가게 삭제")
    public void deleteStore_WhenDeleteStore_ThenReturn200Ok() throws Exception {
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
    public void getMyStoreInfo_WhenGetMyStoreInfos_ThenReturnStoreInfos() throws Exception {
        //given
        when(storeService.getStoresByOwner(anyLong(),any())).thenReturn(MY_STORES_RESPONSE_PAGE);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.
                        get("/api/stores/{storeId}/my?owner-id=1&page=1&size=2&sort=reviewsCount,desc",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-my-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("owner-id").description("가게 주인의 ID"),
                                parameterWithName("page").description("조회할 페이지의 수"),
                                parameterWithName("size").description("검사결과 최대 개수"),
                                parameterWithName("sort").description("정렬기준항목과 정렬순서(콤마로 구분해서 전송)")
                        ),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data[].storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("가게 전화번호").attributes(getStorePhoneFormat()),
                                fieldWithPath("data[].requestDate").type(JsonFieldType.STRING).description("승인 요청한 날짜").attributes(getDateFormat()),
                                fieldWithPath("data[].storeImageUrl").type(JsonFieldType.STRING).description("가게 대표 이미지 URL"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data[].isApproved").type(JsonFieldType.BOOLEAN).description("관리자에 의해 가게가 승인됐는지의 여부"),
                                fieldWithPath("data[].reasonForRejection").type(JsonFieldType.STRING).description("가게가 승인되지 않은 이유"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 페이지 수"),
                                fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("현재 페이지의 아이템 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 개수"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지인지 여부"),
                                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재페이지가 몇번인지")

                        )
                ));

    }

    @Test
    @DisplayName("첫 화면에서 보여줄 가게의 대략적인 정보 - Top rated detail")
    public void getStoreTopInfos_WhenGetStoreTopInfos_ThenReturnStoreInfos() throws Exception {
        //given
        when(storeService.getStoresTopN(StoreSearchType.TOP_RATED, 10)).thenReturn(STORES_TOP10_RESPONSE_LIST);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/stores/top/{storeType}/{count}", "TOP_RATED",10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeType").description("자세히 볼 페이지의 type").attributes(getStoreType()),
                                parameterWithName("count").description("자세히 볼 페이지 개수").attributes(getStoreType())

                        ),
                        responseFields(
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("가게 고유 id"),
                                fieldWithPath("data[].storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data[].avgScore").type(JsonFieldType.NUMBER).description("가게 평점"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data[].storeImageUrl").type(JsonFieldType.STRING).description("가게 대표 이미지 url")
                        )
                ));

    }

    @Test
    @DisplayName("가게 여러건 조회")
    public void getStoreInfosMethod_WhenGetStoreInfos_ThenReturnStoreInfos() throws Exception {
        //given
        StoreResponseWithKeyword storeResponse1 = new StoreResponseWithKeyword(
                1L,
                "Store Name1",
                "123 Store St, City, Country",
                4.5,
                new ArrayList<>(){{add("맛있는"); add("분위기좋은");}},
                "https://example.com/store_image1.jpg"
        );
        StoreResponseWithKeyword storeResponse2 = new StoreResponseWithKeyword(
                2L,
                "Store Name2",
                "123 Store St, City, Country",
                3.5,
                new ArrayList<>(){{add("청결한"); add("분위기좋은");}},
                "https://example.com/store_image2.jpg"
        );
        List<StoreResponseWithKeyword> list = new ArrayList<>(){{
            add(storeResponse1);
            add(storeResponse2);
        }};
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "storeId");

        Page<StoreResponseWithKeyword> page =
                new PageImpl<>(list, pageable, list.size());

        when(storeService.getStores(any(), any(), any(), any())).thenReturn(page);
        //when
        //then
        mockMvc.perform(get("/api/stores/search")
                        .param("category", "양식")
                        .param("search", "분위기가 좋은")
                        .param("location", "전주시 어쩌고 어쩌고")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "reviewsCount,desc")
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-list-search",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("category").description("조회할 가게 카테고리 이름"),
                                parameterWithName("search").description("검색할 가게 이름 키워드"),
                                parameterWithName("location").description("검색하기위한 지역정보(한글이 인코딩되어있을것임)"),
                                parameterWithName("page").description("조회할 페이지의 수"),
                                parameterWithName("size").description("검사결과 최대 개수"),
                                parameterWithName("sort").description("정렬기준항목과 정렬순서(콤마로 구분)")

                        ),
                        responseFields(
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data[].storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data[].avgScore").type(JsonFieldType.NUMBER).description("가게 별점 정보").attributes(getRatingFormat()),
                                fieldWithPath("data[].keywordList").type(JsonFieldType.ARRAY).description("가게 관련 키워드"),
                                fieldWithPath("data[].storeImageUrl").type(JsonFieldType.STRING).description("가게 대표 사진 url"),
                                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 개수"),
                                fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("한 페이지의 데이터개수"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("현재페이지가 첫 페이지인가에 대한 여부"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("현재페이지가 마지막 페이지인가에 대한 여부")

                        )
                ));

    }


    @Test
    @DisplayName("가게 단건 조회")
    public void getStoreInfoMethod_WhenGetStoreInfo_ThenReturnStoreInfo() throws Exception {
        //given
        when(storeService.getStore(any())).thenReturn(STORE_SAVE_UPDATE_RESPONSE);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/stores/{storeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("store/get-one",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("data.storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data.storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data.phone").type(JsonFieldType.STRING).description("가게전화번호").attributes(getStorePhoneFormat())
                        )
                ));

    }
    @Test
    @DisplayName("가게저장시에 요청한정보 + id 정보 반환")
    public void saveStoreMethod_WhenSaveStore_ThenReturnIdAndInfo() throws Exception {
        //given
        when(storeService.save(any(), any(), any(), any(), anyList(), anyList())).thenReturn(STORE_SAVE_UPDATE_RESPONSE);
        //when
        //then
        mockMvc.perform(multipart("/api/stores/categories/{categoryId}", 1L)
                        .file(IMAGE_REQUEST_PART)
                        .file(MENU_REQUEST_PART)
                        .file(BUSINESS_HOUR_REQUEST_PART)
                        .file(KEYWORD_REQUEST_PART)
                        .file(STORE_REQUEST_PART))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("store/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("businessHourRequest").description("가게 영업시간 정보"),
                                partWithName("storeRequest").description("가게의 기본적인 정보"),
                                partWithName("menuRequest").description("메뉴 목록"),
                                partWithName("storeImageList").description("가게 이미지 리스트(Multipartfile형태)"),
                                partWithName("keywordList").description("조회할 키워드 목록")

                        ),
                        responseFields(
                                fieldWithPath("data.storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data.storeName").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data.phone").type(JsonFieldType.STRING).description("가게전화번호").attributes(getStorePhoneFormat())

                        )
                ));

    }
}
