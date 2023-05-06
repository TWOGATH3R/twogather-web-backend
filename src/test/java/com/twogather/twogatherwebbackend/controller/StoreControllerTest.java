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
        when(storeService.getStoresByOwner(anyLong(), anyInt(), anyInt())).thenReturn(MY_STORES_RESPONSE_LIST);
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
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data[].isApproved").type(JsonFieldType.BOOLEAN).description("관리자에 의해 가게가 승인됐는지의 여부"),
                                fieldWithPath("data[].reasonForRejection").type(JsonFieldType.STRING).description("가게가 승인되지 않은 이유")
                        )
                ));

    }

    @Test
    @DisplayName("가게 여러건 조회")
    public void getStoreInfosMethod_WhenGetStoreInfos_ThenReturnStoreInfos() throws Exception {
        //given
        when(storeService.getStores(any(), any(), anyInt(), anyInt(), any(), any())).thenReturn(STORES_RESPONSE_LIST);
        //when
        //then
        mockMvc.perform(get("/api/stores")
                        .param("category", "categoryName1")
                        .param("search", "keyword1")
                        .param("limit", "1")
                        .param("offset", "2")
                        .param("orderBy", "desc")
                        .param("order", "rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("category").description("조회할 가게 카테고리 이름"),
                                parameterWithName("search").description("검색할 가게 이름 키워드"),
                                parameterWithName("limit").description("한 페이지에서 조회할 가게 수"),
                                parameterWithName("offset").description("조회할 가게 리스트에서의 시작 위치"),
                                parameterWithName("orderBy").description("가게 조회 결과를 정렬 기준 항목"),
                                parameterWithName("order").description("가게 조회 결과를 정렬 순서")
                        ),
                        responseFields(
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("가게이름"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게주소"),
                                fieldWithPath("data[].rating").type(JsonFieldType.NUMBER).description("가게 별점 정보").attributes(getRatingFormat())
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
