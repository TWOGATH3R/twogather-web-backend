package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getUsernameFormat;
import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(EmailController.class)
public class EmailControllerTest extends ControllerTest{
    @MockBean
    private EmailService emailService;


    @Test
    @DisplayName("인증번호 받기")
    public void sendMail_WhenSendMail_ThenSendMailAndReturnToken() throws Exception {
        //given
        when(emailService.sendCodeEmail(any())).thenReturn(VERIFICATION_CODE_RESPONSE);
        //when
        //then
        mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(EMAIL_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("email/post",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("인증메일을 전송받을 이메일주소")
                        ),
                        responseFields(
                                fieldWithPath("data.verificationCode").type(JsonFieldType.STRING).description("인증번호: 6자리로 구성되어있음")
                        )
                ));

    }

    @Test
    @DisplayName("임시 비밀번호 발급 이메일 전송")
    public void sendPasswordChangeEmail_WhenSendMail_ThenSendMailAndReturnToken() throws Exception {
        //given
       doNothing().when(emailService).requestEmailChangePassword(any());
        //when
        //then
        mockMvc.perform(post("/api/email/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(FIND_PASSWORD_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("email/password-post",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("가입된 이메일주소"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("가입한 아이디").attributes(getUsernameFormat())
                        )
                ));

    }
}
