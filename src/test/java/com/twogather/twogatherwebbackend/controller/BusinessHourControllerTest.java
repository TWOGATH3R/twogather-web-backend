package com.twogather.twogatherwebbackend.controller;

import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getDayOfWeekFormat;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getTimeFormat;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateInfo;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
import com.twogather.twogatherwebbackend.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(BusinessHourController.class)
public class BusinessHourControllerTest extends ControllerTest{

    @MockBean
    private BusinessHourService businessHourService;

    private static final String URL = "/api/stores/{storeId}/business-hours";



    @Test
    @DisplayName("영업시간 정보 여러개에 대해 업데이트를 요청했을때 변경된 정보 반환")
    public void updateList_WhenUpdateBusinessHours_ThenReturnInfo() throws Exception {
        //given
        when(businessHourService.setList(anyLong(), any())).thenReturn(createBusinessHourSaveResponse());
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.put(URL, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(createBusinessHourSaveRequest()))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("business-hour/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 ID")
                        ),
                        requestFields(
                               fieldWithPath("businessHourList[].dayOfWeek").type(JsonFieldType.STRING).description("The day of week.").attributes(getDayOfWeekFormat()),
                                fieldWithPath("businessHourList[].startTime").type(JsonFieldType.STRING).description("The opening time of the business hour").attributes(getTimeFormat()).optional(),
                                fieldWithPath("businessHourList[].endTime").type(JsonFieldType.STRING).description("The closing time of the business hour").attributes(getTimeFormat()).optional(),
                                fieldWithPath("businessHourList[].hasBreakTime").type(JsonFieldType.BOOLEAN).description("해당 요일의 breakTime 존재 여부"),
                                fieldWithPath("businessHourList[].breakStartTime").type(JsonFieldType.STRING).description("breakTime의 시작 시간").attributes(getTimeFormat()).optional(),
                                fieldWithPath("businessHourList[].breakEndTime").type(JsonFieldType.STRING).description("breakTime이 끝나는 시간").attributes(getTimeFormat()).optional(),
                                fieldWithPath("businessHourList[].isOpen").type(JsonFieldType.BOOLEAN).description("해당 요일의 장사여부")
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

    @Test
    @DisplayName("영업시간 정보 storeId로 전체 (7건) 조회")
    public void getBusinessHourInfo_WhenGetBusinessHourInfo_ThenReturn7Info() throws Exception {
        //given
        when(businessHourService.findBusinessHoursByStoreId(any()))
                .thenReturn(BUSINESS_HOUR_RESPONSE_LIST);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("business-hour/get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 id")
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
    private BusinessHourSaveUpdateListRequest createBusinessHourSaveRequest(){
        List<BusinessHourSaveUpdateInfo> businessHourRequests = new ArrayList<>();

        for (DayOfWeek dayOfWeek = DayOfWeek.MONDAY; dayOfWeek.compareTo(DayOfWeek.FRIDAY) <= 0; dayOfWeek = dayOfWeek.plus(1)) {
            BusinessHourSaveUpdateInfo businessHourRequest = new BusinessHourSaveUpdateInfo(
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0),
                    dayOfWeek,
                    true,
                    false,
                    null,
                    null
            );

            businessHourRequests.add(businessHourRequest);
        }
        return new BusinessHourSaveUpdateListRequest(businessHourRequests);
    }
    private List<BusinessHourResponse> createBusinessHourSaveResponse(){
        List<BusinessHourResponse> businessHours = new ArrayList<>();
        long id = 1l;
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            boolean isOpen = dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
            LocalTime startTime = isOpen ? LocalTime.of(9, 0) : null;
            LocalTime endTime = isOpen ? LocalTime.of(18, 0) : null;

            BusinessHourResponse businessHour = BusinessHourResponse.builder()
                    .businessHourId(id++)
                    .storeId(1L)
                    .startTime(startTime)
                    .endTime(endTime)
                    .dayOfWeek(dayOfWeek)
                    .isOpen(isOpen)
                    .hasBreakTime(false)
                    .breakStartTime(null)
                    .breakEndTime(null)
                    .build();

            businessHours.add(businessHour);
        }
        return businessHours;
    }

}