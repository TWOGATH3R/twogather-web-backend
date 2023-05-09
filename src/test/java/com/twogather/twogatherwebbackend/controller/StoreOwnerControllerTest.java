package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(StoreOwnerController.class)
public class StoreOwnerControllerTest extends ControllerTest{
    @MockBean
    private StoreOwnerService storeOwnerService;

    @Test
    @DisplayName("가게주인탈퇴")
    public void leave_WhenOwnerLeave_Then200Ok() throws Exception {
        //given
        doNothing().when(storeOwnerService).delete(anyLong());
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/owners/{ownerId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(document("owner/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("ownerId").description("사업자의 고유 id")
                )));

    }

    @Test
    @DisplayName("가게주인정보조회")
    public void getOwnerInfo_WhenGetOwnerInfo_ThenReturnOwnerInfo() throws Exception {
        //given
        when(storeOwnerService.getMemberWithAuthorities(anyLong())).thenReturn(STORE_OWNER_RESPONSE);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/owners/{ownerId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(document("owner/get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("ownerId").description("사업자의 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("data.businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("data.businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("data.businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        )
                ));

    }

    @Test
    @DisplayName("가게주인정보업데이트")
    public void updateOwnerInfo_WhenUpdateOwnerInfo_ThenReturnOwnerInfo() throws Exception {
        //given
        when(storeOwnerService.update(any())).thenReturn(STORE_OWNER_RESPONSE);
        //when
        //then

        mockMvc.perform(put("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(STORE_OWNER_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("owner/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("계정 비밀번호").attributes(getPasswordFormat()),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("data.businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("data.businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("data.businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        )
                ));

    }

    @Test
    @DisplayName("가게주인등록")
    public void join_WhenOwnerSave_ThenReturnOwnerInfo() throws Exception {
        //given
        when(storeOwnerService.join(any())).thenReturn(STORE_OWNER_RESPONSE);
        //when
        //then

        mockMvc.perform(post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(STORE_OWNER_REQUEST))
                )
                .andExpect(status().isCreated())
                .andDo(document("owner/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("계정 비밀번호").attributes(getPasswordFormat()),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명"),
                                fieldWithPath("data.businessNumber").type(JsonFieldType.STRING).description("사업자번호").attributes(getBusinessNumberFormat()),
                                fieldWithPath("data.businessName").type(JsonFieldType.STRING).description("사업자이름"),
                                fieldWithPath("data.businessStartDate").type(JsonFieldType.STRING).description("사업시작일").attributes(getDateFormat())

                        )
                ));

    }


}
