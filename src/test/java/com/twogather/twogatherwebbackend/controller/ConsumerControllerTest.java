package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.service.ConsumerService;
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
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getPasswordFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ConsumerController.class)
public class ConsumerControllerTest extends ControllerTest {
    @MockBean
    private ConsumerService consumerService;

    private static final String URL = "/api/consumers/{memberId}";
    @Test
    @DisplayName("소비자 탈퇴")
    public void leave_WhenConsumerLeave_Then200Ok() throws Exception {
        //given
        doNothing().when(consumerService).delete(anyLong());
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(CONSUMER_SAVE_UPDATE_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("consumer/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("고객의 고유 id")
                        )
                ));

    }

    @Test
    @DisplayName("소비자 정보 조회")
    public void getConsumerInfo_WhenGetConsumerInfo_Then200Ok() throws Exception {
        //given
        when(consumerService.getConsumerInfo(anyLong())).thenReturn(CONSUMER_RESPONSE);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.get(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(CONSUMER_SAVE_UPDATE_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("consumer/get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("고객의 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat()),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명")
                        )
                ));

    }

    @Test
    @DisplayName("소비자 정보 변경")
    public void update_WhenConsumerInfoChange_Then200Ok() throws Exception {
        //given
        when(consumerService.update(any())).thenReturn(CONSUMER_RESPONSE);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.put(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(CONSUMER_SAVE_UPDATE_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("consumer/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("고객의 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat()),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("계정 비밀번호").attributes(getPasswordFormat()),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명")
                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat()),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명")
                        )
                ));

    }

    @Test
    @DisplayName("소비자 가입")
    public void join_WhenConsumerJoin_Then201() throws Exception {
        //given
        when(consumerService.join(any())).thenReturn(CONSUMER_RESPONSE);
        //when
        //then

        mockMvc.perform(post("/api/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(CONSUMER_SAVE_UPDATE_REQUEST))
                )
                .andExpect(status().isCreated())
                .andDo(document("consumer/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat()),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("계정 비밀번호").attributes(getPasswordFormat()),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자명")
                        ),
                        responseFields(
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("로그인 ID").attributes(getUsernameFormat()),
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("사업자의 고유 id"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자명")
                        )
                ));

    }



}
