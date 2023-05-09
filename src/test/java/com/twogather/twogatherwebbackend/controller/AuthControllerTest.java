package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.auth.JwtFilter;
import com.twogather.twogatherwebbackend.service.AuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
public class AuthControllerTest extends ControllerTest{


    @Test
    public void login() throws Exception {
        //given
        when(authService.login(any())).thenReturn(TOKEN_AND_ID);
        //when
        mockMvc.perform(post("/api/login/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(LOGIN_REQUEST))
                )
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtFilter.AUTHORIZATION_HEADER))
                .andExpect(header().string(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + TOKEN_AND_ID.getToken()))
                .andDo(document("auth/login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("로그인에 필요한 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("로그인에 필요한 비밀번호").attributes(getPasswordFormat())
                          ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("로그인한 회원의 고유 id")
                        )
                        ));

    }


}