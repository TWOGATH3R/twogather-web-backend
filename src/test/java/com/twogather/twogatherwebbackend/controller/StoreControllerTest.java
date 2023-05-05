package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.dto.store.StoreResponse;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
import com.twogather.twogatherwebbackend.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.*;
import static groovy.util.FactoryBuilderSupport.OWNER;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(StoreController.class)
public class StoreControllerTest extends ControllerTest{
    @MockBean
    private StoreService storeService;

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
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-my-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
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
        mockMvc.perform(get("/api/stores?category=categoryName1&search=keyword1&limit=1&offset=2&orderBy=desc&order=rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
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
        mockMvc.perform(get("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(document("store/get-one",
                        getDocumentRequest(),
                        getDocumentResponse(),
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
                        .with(csrf())
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
