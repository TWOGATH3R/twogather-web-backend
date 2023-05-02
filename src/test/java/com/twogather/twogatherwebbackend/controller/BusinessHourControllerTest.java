package com.twogather.twogatherwebbackend.controller;

import static com.twogather.twogatherwebbackend.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.DocumentFormatGenerator.getTimeFormat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.auth.JwtAuthenticationEntryPoint;
import com.twogather.twogatherwebbackend.auth.JwtFilter;
import com.twogather.twogatherwebbackend.auth.TokenProvider;
import com.twogather.twogatherwebbackend.config.JwtSecurityConfig;
import com.twogather.twogatherwebbackend.config.SecurityConfig;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.service.AuthService;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BusinessHourController.class)
@AutoConfigureRestDocs
public class BusinessHourControllerTest {
    @Autowired
    private MockMvc mockMvc;

    //없으면 businessHourService 의존성 없다는 error가 뜸
    @MockBean
    private BusinessHourService businessHourService;

    private ObjectMapper objectMapper = new ObjectMapper();

    //없으면 jwtfilter에 의존성 없다는 error가 뜸
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("save: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void WhenValidRequest_ThenResponse() throws Exception {
        //given
        String token = "FA6EWZ0qLACEZWOr9QqxwdDygf";
        when(authService.login(any())).thenReturn(new AuthService.TokenAndId(token, 1l));
        when(tokenProvider.validateToken(token)).thenReturn(true);
        //when
        //then
        mockMvc.perform(post("/api/business-hour")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(BUSINESS_HOUR_SAVE_REQUEST)))
                .andExpect(status().isOk())
                .andDo(document("business-hour/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER).description("The id of the store"),
                                fieldWithPath("dayOfWeek").type(JsonFieldType.STRING).description("The day of week"),
                                fieldWithPath("startTime").type(JsonFieldType.STRING).attributes(getTimeFormat()).description("The opening time of the business hour").optional(),
                                fieldWithPath("endTime").type(JsonFieldType.STRING).attributes(getTimeFormat()).description("The closing time of the business hour").optional(),
                                fieldWithPath("hasBreakTime").type(JsonFieldType.BOOLEAN).description("해당 요일의 breakTime 존재 여부"),
                                fieldWithPath("breakStartTime").type(JsonFieldType.STRING).attributes(getTimeFormat()).description("breakTime의 시작 시간").optional(),
                                fieldWithPath("breakEndTime").type(JsonFieldType.STRING).attributes(getTimeFormat()).description("breakTime이 끝나는 시간").optional(),
                                fieldWithPath("open").type(JsonFieldType.BOOLEAN).description("해당 요일의 장사여부")
                        ),
                        responseFields(
                                fieldWithPath("data.businessHourId").type(JsonFieldType.NUMBER).description("The id of the businessHour"),
                                fieldWithPath("data.storeId").type(JsonFieldType.NUMBER).description("The id of the store"),
                                fieldWithPath("data.dayOfWeek").type(JsonFieldType.STRING).description("The day of week"),
                                fieldWithPath("data.startTime").type(JsonFieldType.STRING).attributes(getTimeFormat()).description("The opening time of the business hour").optional(),
                                fieldWithPath("data.endTime").type(JsonFieldType.STRING).attributes(getTimeFormat()).description("The closing time of the business hour").optional(),
                                fieldWithPath("data.hasBreakTime").type(JsonFieldType.BOOLEAN).description("해당 요일의 breakTime 존재 여부"),
                                fieldWithPath("data.breakStartTime").type(JsonFieldType.STRING).attributes(getTimeFormat()).description("breakTime의 시작 시간").optional(),
                                fieldWithPath("data.breakEndTime").type(JsonFieldType.STRING).attributes(getTimeFormat()).description("breakTime이 끝나는 시간").optional(),
                                fieldWithPath("data.open").type(JsonFieldType.BOOLEAN).description("해당 요일의 장사여부")

                        )
                ));

    }
}