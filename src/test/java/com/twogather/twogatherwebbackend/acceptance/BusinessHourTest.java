package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.controller.BusinessHourController;
import com.twogather.twogatherwebbackend.controller.ControllerTest;
import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Category;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.dto.category.CategoryResponse;
import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.TestConstants.OWNER_SAVE_REQUEST2;
import static com.twogather.twogatherwebbackend.acceptance.TestHelper.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BusinessHourTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StoreOwnerRepository ownerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private BusinessHourRepository businessHourRepository;
    @Autowired
    private BusinessHourService businessHourService;

    private StoreOwner owner;
    private Store store;

    @BeforeEach
    public void setup(){
        owner = createOwner(ownerRepository, passwordEncoder);
        store = createStore(storeRepository,owner);
        createAuthority(owner);
    }
    @Test
    @Transactional
    @DisplayName("열린 날만 요청해도 영업안하는요일까지 포함해서 응답으로 줘야한다")
    public void whenOnlyOpenDaysProvided_thenResponseIncludesClosedDays() throws Exception {
        //given
        BusinessHourSaveRequest businessHour1 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        BusinessHourSaveRequest businessHour2 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.THURSDAY, true,
                false, null,null
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        businessHourList.add(businessHour2);
        BusinessHourController.BusinessHourSaveListRequest request = new BusinessHourController.BusinessHourSaveListRequest(businessHourList);

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", hasSize(7)))
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'MONDAY' && @.isOpen == true && @.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'TUESDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'WEDNESDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'THURSDAY' && @.isOpen == true && @.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'FRIDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'SATURDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'SUNDAY' && @.isOpen == false)]").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    @DisplayName("모든요일에 대해 열린날을 요청하면 응답으로 닫힌날을 제공하면안된다")
    public void whenAllOpenDaysProvided_thenResponseIncludesNoClosedDays() throws Exception {
        //given
        BusinessHourSaveRequest businessHour1 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        BusinessHourSaveRequest businessHour2 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.TUESDAY, true,
                false, null,null
        );
        BusinessHourSaveRequest businessHour3 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.WEDNESDAY, true,
                false, null,null
        );
        BusinessHourSaveRequest businessHour4 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.THURSDAY, true,
                false, null,null
        );
        BusinessHourSaveRequest businessHour5 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.SATURDAY, true,
                false, null,null
        );
        BusinessHourSaveRequest businessHour6 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.FRIDAY, true,
                false, null,null
        );
        BusinessHourSaveRequest businessHour7 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.SUNDAY, true,
                false, null,null
        );

        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        businessHourList.add(businessHour2);
        businessHourList.add(businessHour3);
        businessHourList.add(businessHour4);
        businessHourList.add(businessHour5);
        businessHourList.add(businessHour6);
        businessHourList.add(businessHour7);


        BusinessHourController.BusinessHourSaveListRequest request = new BusinessHourController.BusinessHourSaveListRequest(businessHourList);

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", hasSize(7)))
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'MONDAY' && @.isOpen == true && @.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'TUESDAY' && @.isOpen == true&& @.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'WEDNESDAY' && @.isOpen == true&& @.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'THURSDAY' && @.isOpen == true && @.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'FRIDAY' && @.isOpen == true&& @.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'SATURDAY' && @.isOpen == true&& @.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'SUNDAY' && @.isOpen == true&& @.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    @DisplayName("영업시작시간이 영업종료시간보다 나중이라면 exception을 throw해야한다")
    public void whenStartTimeIsLaterThanEndTime_thenThrowException() throws Exception {
        //given
        BusinessHourSaveRequest businessHour1 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(16,0), java.time.LocalTime.of(9,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveListRequest request = new BusinessHourController.BusinessHourSaveListRequest(businessHourList);

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("시작시간은 종료시간보다 먼저여야합니다"))
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    @Transactional
    @DisplayName("브레이크시작시간이 브레이크종료시간보다 나중이라면 exception을 throw해야한다")
    public void whenBreakStartTimeIsLaterThanEndTime_thenThrowException() throws Exception {
        //given
        BusinessHourSaveRequest businessHour1 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(11,0)
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveListRequest request = new BusinessHourController.BusinessHourSaveListRequest(businessHourList);

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("시작시간은 종료시간보다 먼저여야합니다"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    @DisplayName("만약 hasbreaktime을 true로 설정해놨는데 starttime이나 endtime중에 하나를 null넣으면 exception throw")
    public void whenValidateBreakTimeNull_thenThrowException() throws Exception {
        //given
        BusinessHourSaveRequest businessHour1 = new BusinessHourSaveRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                true, null,null
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveListRequest request = new BusinessHourController.BusinessHourSaveListRequest(businessHourList);

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("시작시작과 종료시간을 설정해주세요"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    @DisplayName("만약 isopen을 true로 설정해놨는데 starttime이나 endtime중에 하나를 null넣으면 exception throw")
    public void whenValidateOpenEndTimeNull_thenThrowException() throws Exception {
        //given
        BusinessHourSaveRequest businessHour1 = new BusinessHourSaveRequest(
                store.getStoreId(),null, java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveListRequest request = new BusinessHourController.BusinessHourSaveListRequest(businessHourList);

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("시작시작과 종료시간을 설정해주세요"))
                .andDo(MockMvcResultHandlers.print());
    }
}
