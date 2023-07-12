package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.*;
import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StoreOwnerRepository ownerRepository;
    @Autowired
    private PrivateConstants constants;

    @Test
    public void whenLoginThenSuccess() throws Exception {
        //given
        initSetting();
        //when
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(LOGIN_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("auth/login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("로그인에 필요한 로그인 아이디").attributes(getUsernameFormat()),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("로그인에 필요한 비밀번호").attributes(getPasswordFormat())
                        ),
                        responseFields(
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("로그인한 회원의 고유 id"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("로그인한 회원의 닉네임").attributes(getMemberNameFormat())

                        )
                ));

    }

    @Test
    public void when() throws Exception {
        //given
        initSetting();
        MvcResult result = login();
        String refreshToken = result.getResponse().getHeader(constants.REFRESH_TOKEN_HEADER);
        //when
        mockMvc.perform(get("/api/access-token")
                .header(constants.REFRESH_TOKEN_HEADER, refreshToken))
                .andExpect(status().isOk())
                .andDo(document("auth/access-token",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));

    }
    private void initSetting(){
        StoreOwner owner1 = STORE_OWNER;
        ownerRepository.save(owner1);
    }
    private MvcResult login() throws Exception {
        return mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(LOGIN_REQUEST)))
                .andReturn();
    }
}