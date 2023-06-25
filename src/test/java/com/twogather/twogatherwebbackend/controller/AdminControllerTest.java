package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.*;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getStorePhoneFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminController.class)
public class AdminControllerTest extends ControllerTest{
    @MockBean
    private StoreService storeService;

    @Test
    @DisplayName("관리자 가게 조회")
    public void whenFindStoreByAdmin_ThenSuccess() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        String url = "/api/admin/stores/{type}";

        when(storeService.getStores(StoreStatus.PENDING, pageable)).thenReturn(new PageImpl<>(MY_STORE_RESPONSES, pageable, MY_STORE_RESPONSES.size()));
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get(url, StoreStatus.PENDING)
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andDo(document("admin/getStore",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("type").description("읽어올 가게의 타입").attributes(getStoreStatus())
                        ),
                        requestParameters(
                                parameterWithName("page").description("조회할 페이지"),
                                parameterWithName("size").description("검사결과 최대 개수")
                        ),
                        responseFields(
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("가게 전화 번호").attributes(getStorePhoneFormat()),
                                fieldWithPath("data[].requestDate").type(JsonFieldType.STRING).description("가게 승인 요청 날짜").attributes(getDateFormat()),
                                fieldWithPath("data[].reasonForRejection").type(JsonFieldType.STRING).description("가게 거부 이유"),
                                fieldWithPath("data[].storeImageUrl").type(JsonFieldType.STRING).description("가게 대표 사진 url"),
                                fieldWithPath("data[].isApproved").type(JsonFieldType.BOOLEAN).description("가게 상태").attributes(getStoreStatus()),
                                fieldWithPath("data[].storeName").type(JsonFieldType.STRING).description("가게 이름"),
                                fieldWithPath("data[].address").type(JsonFieldType.STRING).description("가게 주소"),
                                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 개수"),
                                fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("한 페이지의 데이터개수"),
                                fieldWithPath("isFirst").type(JsonFieldType.BOOLEAN).description("현재페이지가 첫 페이지인가에 대한 여부"),
                                fieldWithPath("isLast").type(JsonFieldType.BOOLEAN).description("현재페이지가 마지막 페이지인가에 대한 여부")
                        )
                ));

    }

    @Test
    @DisplayName("관리자가 가게 거부")
    public void whenStoreStatusDeny_ThenSuccess() throws Exception {
        //given
        String url = "/api/admin/stores/reject/{storeId}";
        doNothing().when(storeService).rejectStore(anyLong(), any());
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(url, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .writeValueAsString(STORE_REJECT_REASON))
                )
                .andExpect(status().isOk())
                .andDo(document("admin/reject",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("reason").type(JsonFieldType.STRING).description("가게 거절 사유")
                        )
                ));

    }

    @Test
    @DisplayName("관리자가 가게 승인")
    public void whenStoreStatusApprove_ThenSuccess() throws Exception {
        //given
        String url = "/api/admin/stores/approve/{storeId}";
        doNothing().when(storeService).approveStore(anyLong());
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(url, 1))
                .andExpect(status().isOk())
                .andDo(document("admin/approve",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 id")
                        )
                ));

    }
}
