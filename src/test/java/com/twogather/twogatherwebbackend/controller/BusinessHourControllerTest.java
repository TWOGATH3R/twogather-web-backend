package com.twogather.twogatherwebbackend.controller;

import static com.twogather.twogatherwebbackend.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.DocumentFormatGenerator.getDayOfWeekFormat;
import static com.twogather.twogatherwebbackend.DocumentFormatGenerator.getTimeFormat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(BusinessHourController.class)
public class BusinessHourControllerTest extends ControllerTest{

    @MockBean
    private BusinessHourService businessHourService;

    @Test
    @DisplayName("영업시간 정보 여러개에 대해 저장을 요청했을때 201 반환")
    public void WhenSaveBusinessHours_Then201Status() throws Exception {
        //given
        when(businessHourService.saveList(any())).thenReturn(BUSINESS_HOUR_SAVE_RESPONSE_LIST);
        //when
        //then
        mockMvc.perform(post("/api/business-hours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(BUSINESS_HOUR_SAVE_REQUEST_LIST))
                )
                .andExpect(status().isCreated())
                .andDo(document("business-hours/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("[].storeId").type(JsonFieldType.NUMBER).description("The id of the store"),
                                fieldWithPath("[].dayOfWeek").type(JsonFieldType.STRING).description("The day of week.").attributes(getDayOfWeekFormat()),
                                fieldWithPath("[].startTime").type(JsonFieldType.STRING).description("The opening time of the business hour").attributes(getTimeFormat()).optional(),
                                fieldWithPath("[].endTime").type(JsonFieldType.STRING).description("The closing time of the business hour").attributes(getTimeFormat()).optional(),
                                fieldWithPath("[].hasBreakTime").type(JsonFieldType.BOOLEAN).description("해당 요일의 breakTime 존재 여부"),
                                fieldWithPath("[].breakStartTime").type(JsonFieldType.STRING).description("breakTime의 시작 시간").attributes(getTimeFormat()).optional(),
                                fieldWithPath("[].breakEndTime").type(JsonFieldType.STRING).description("breakTime이 끝나는 시간").attributes(getTimeFormat()).optional(),
                                fieldWithPath("[].isOpen").type(JsonFieldType.BOOLEAN).description("해당 요일의 장사여부")
                        ),
                        responseFields(
                                fieldWithPath("data[].businessHourId").type(JsonFieldType.NUMBER).description("The id of the businessHour"),
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("The id of the store"),
                                fieldWithPath("data[].dayOfWeek").type(JsonFieldType.STRING).description("The day of week").attributes(getDayOfWeekFormat()),
                                fieldWithPath("data[].startTime").type(JsonFieldType.STRING).description("The opening time of the business hour").attributes(getTimeFormat()).optional(),
                                fieldWithPath("data[].endTime").type(JsonFieldType.STRING).description("The closing time of the business hour").attributes(getTimeFormat()).optional(),
                                fieldWithPath("data[].hasBreakTime").type(JsonFieldType.BOOLEAN).description("해당 요일의 breakTime 존재 여부"),
                                fieldWithPath("data[].breakStartTime").type(JsonFieldType.STRING).description("breakTime의 시작 시간").attributes(getTimeFormat()).optional(),
                                fieldWithPath("data[].breakEndTime").type(JsonFieldType.STRING).description("breakTime이 끝나는 시간").attributes(getTimeFormat()).optional(),
                                fieldWithPath("data[].isOpen").type(JsonFieldType.BOOLEAN).description("해당 요일의 장사여부")

                        )
                ));

    }

}