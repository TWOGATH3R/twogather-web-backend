package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.member.VerifyPasswordRequest;
import com.twogather.twogatherwebbackend.service.ConsumerService;
import com.twogather.twogatherwebbackend.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

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
    @MockBean
    private MemberService memberService;

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
        MemberResponse response = new MemberResponse(1l, "user1", "adsd@naver.cin","홍길동");
        when(consumerService.getConsumerInfo(anyLong())).thenReturn(response);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.get(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(new MemberSaveUpdateRequest(response.getEmail(), response.getUsername(), "password1", response.getName())))
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
        MemberResponse response = new MemberResponse(1l, "user1", "adsd@naver.cin","홍길동");
        MemberSaveUpdateRequest request = new MemberSaveUpdateRequest(response.getEmail(), response.getUsername(), "password1", response.getName());
        when(memberService.update(any())).thenReturn(response);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.put(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(request))
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
        MemberResponse response = new MemberResponse(1l, "user1", "adsd@naver.cin","홍길동");
        when(consumerService.join(any())).thenReturn(response);
        MemberSaveUpdateRequest request = new MemberSaveUpdateRequest(response.getEmail(), response.getUsername(), "password1", response.getName());

        //when
        //then

        mockMvc.perform(post("/api/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(request))
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

    @Test
    @DisplayName("비밀번호 검증")
    public void WhenVerifyPassword_ThenReturnTrueOrFalse() throws Exception {
        //given
        when(memberService.verifyPassword(any())).thenReturn(true);
        //when
        //then

        mockMvc.perform(post("/api/consumers/verify-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .writeValueAsString(new VerifyPasswordRequest("passsword1"))))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("consumer/verify-password",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("기존 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("data.isValid").type(JsonFieldType.BOOLEAN).description("비밀번호 일치 여부")
                        )
                ));

    }



}
