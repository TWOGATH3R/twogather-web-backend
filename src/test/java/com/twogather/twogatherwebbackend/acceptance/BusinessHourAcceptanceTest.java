package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.controller.BusinessHourController;
import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateRequest;
import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
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
import java.util.ArrayList;

import static com.twogather.twogatherwebbackend.acceptance.TestHelper.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BusinessHourAcceptanceTest {
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
    @DisplayName("save: 열린 날만 요청해도 영업안하는요일까지 포함해서 응답으로 줘야한다")
    public void whenOnlyOpenDaysProvided_thenResponseIncludesClosedDays() throws Exception {
        //given
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour2 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.THURSDAY, true,
                false, null,null
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        businessHourList.add(businessHour2);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

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
    @DisplayName("save: 모든요일에 대해 열린날을 요청하면 응답으로 닫힌날을 제공하면안된다")
    public void whenAllOpenDaysProvided_thenResponseIncludesNoClosedDays() throws Exception {
        //given
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour2 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.TUESDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour3 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.WEDNESDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour4 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.THURSDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour5 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.SATURDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour6 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.FRIDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour7 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.SUNDAY, true,
                false, null,null
        );

        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        businessHourList.add(businessHour2);
        businessHourList.add(businessHour3);
        businessHourList.add(businessHour4);
        businessHourList.add(businessHour5);
        businessHourList.add(businessHour6);
        businessHourList.add(businessHour7);


        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

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
    @DisplayName("save: 영업시작시간이 영업종료시간보다 나중이라면 exception을 throw해야한다")
    public void whenStartTimeIsLaterThanEndTime_thenThrowException() throws Exception {
        //given
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(16,0), java.time.LocalTime.of(9,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

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
    @DisplayName("save: 브레이크시작시간이 브레이크종료시간보다 나중이라면 exception을 throw해야한다")
    public void whenBreakStartTimeIsLaterThanEndTime_thenThrowException() throws Exception {
        //given
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(11,0)
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

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
    @DisplayName("save: 만약 hasbreaktime을 true로 설정해놨는데 starttime이나 endtime중에 하나를 null넣으면 exception throw")
    public void whenValidateBreakTimeNull_thenThrowException() throws Exception {
        //given
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                true, null,null
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

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
    @DisplayName("save: 만약 isopen을 true로 설정해놨는데 starttime이나 endtime중에 하나를 null넣으면 exception throw")
    public void whenValidateOpenEndTimeNull_thenThrowException() throws Exception {
        //given
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(),null, java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

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
    @DisplayName("update: 요청한 날에 대해서만 UPDATE 수행한다. 요청하지 않은 날에 대해서는 삭제나 수정을 진행하지 않는다")
    public void whenOnlyUpdateDaysProvided_thenReviseProvidedDays() throws Exception {
        //given
        BusinessHour businessHour1 = new BusinessHour(
                store, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.MONDAY, true,
                false, null, null
        );
        BusinessHour businessHour2 = new BusinessHour(
                store, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.TUESDAY, true,
                false, null, null
        );
        BusinessHour businessHour3 = new BusinessHour(
                store, null,null, DayOfWeek.WEDNESDAY, false,
                false, null, null
        );
        BusinessHour businessHour4 = new BusinessHour(
                store, null,null, DayOfWeek.THURSDAY, false,
                false, null, null
        );
        BusinessHour businessHour5 = new BusinessHour(
                store, null,null, DayOfWeek.FRIDAY, false,
                false, null, null
        );
        BusinessHour businessHour6 = new BusinessHour(
                store, null,null, DayOfWeek.SUNDAY, false,
                false, null, null
        );
        BusinessHour businessHour7 = new BusinessHour(
                store, null,null, DayOfWeek.SATURDAY, false,
                false, null, null
        );
        BusinessHour savedBusinessHour1 = businessHourRepository.save(businessHour1);
        businessHourRepository.save(businessHour2);
        businessHourRepository.save(businessHour3);
        businessHourRepository.save(businessHour4);
        businessHourRepository.save(businessHour5);
        businessHourRepository.save(businessHour6);
        businessHourRepository.save(businessHour7);

        BusinessHourSaveUpdateRequest request1 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), java.time.DayOfWeek.MONDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(request1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(7)))
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'MONDAY' && @.isOpen == true && @.startTime == '11:00' && @.endTime == '17:00' && @.hasBreakTime == true && @.breakStartTime =='12:00' && @.breakEndTime =='13:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'TUESDAY' && @.isOpen == true && @.startTime == '09:00' && @.endTime == '16:00' )]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'WEDNESDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'THURSDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'FRIDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'SATURDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'SUNDAY' && @.isOpen == false)]").exists())

                .andDo(MockMvcResultHandlers.print());
    }
    //만약 업데이트해서 중복된 요일이 나오면 exception 발생
    @Test
    @Transactional
    @DisplayName("update: 업데이트를 요청한 결과 해당 가게에 중복되는 요일이 생겨버린다면 예외 throw")
    public void whenUpdateDuplicatedDayOfWeek_thenThrowsException() throws Exception {
        //given
        BusinessHour businessHour1 = new BusinessHour(
                store, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.MONDAY, true,
                false, null, null
        );
        BusinessHour businessHour2 = new BusinessHour(
                store, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.TUESDAY, true,
                false, null, null
        );
        BusinessHour businessHour3 = new BusinessHour(
                store, null,null, DayOfWeek.WEDNESDAY, false,
                false, null, null
        );
        BusinessHour businessHour4 = new BusinessHour(
                store, null,null, DayOfWeek.THURSDAY, false,
                false, null, null
        );
        BusinessHour businessHour5 = new BusinessHour(
                store, null,null, DayOfWeek.FRIDAY, false,
                false, null, null
        );
        BusinessHour businessHour6 = new BusinessHour(
                store, null,null, DayOfWeek.SUNDAY, false,
                false, null, null
        );
        BusinessHour businessHour7 = new BusinessHour(
                store, null,null, DayOfWeek.SATURDAY, false,
                false, null, null
        );
        BusinessHour savedBusinessHour1 = businessHourRepository.save(businessHour1);
        businessHourRepository.save(businessHour2);
        businessHourRepository.save(businessHour3);
        businessHourRepository.save(businessHour4);
        businessHourRepository.save(businessHour5);
        businessHourRepository.save(businessHour6);
        businessHourRepository.save(businessHour7);

        DayOfWeek duplicatedDayOfWeek = DayOfWeek.THURSDAY;
        BusinessHourSaveUpdateRequest duplicatedDayOfWeekRequest1 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), duplicatedDayOfWeek, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        BusinessHourSaveUpdateRequest duplicatedDayOfWeekRequest2 = new BusinessHourSaveUpdateRequest(
                store.getStoreId(), java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), duplicatedDayOfWeek, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        Long storeId = store.getStoreId();
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(duplicatedDayOfWeekRequest1);
        businessHourList.add(duplicatedDayOfWeekRequest2);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("중복되는 요일이 있습니다"))
                .andDo(MockMvcResultHandlers.print());
    }


    //get
    @Test
    @Transactional
    @DisplayName("get: 가게 id에 해당하는 영업시간 7개 정보를 모두 가져온다")
    public void whenGetBusinessHourByStoreId_thenReturnAllDayOfWeekBusinessHour() throws Exception {
        //given
        BusinessHour businessHour1 = new BusinessHour(
                store, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.MONDAY, true,
                false, null, null
        );
        BusinessHour businessHour2 = new BusinessHour(
                store, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.TUESDAY, true,
                false, null, null
        );
        BusinessHour businessHour3 = new BusinessHour(
                store, null,null, DayOfWeek.WEDNESDAY, false,
                false, null, null
        );
        BusinessHour businessHour4 = new BusinessHour(
                store, null,null, DayOfWeek.THURSDAY, false,
                false, null, null
        );
        BusinessHour businessHour5 = new BusinessHour(
                store, null,null, DayOfWeek.FRIDAY, false,
                false, null, null
        );
        BusinessHour businessHour6 = new BusinessHour(
                store, null,null, DayOfWeek.SUNDAY, false,
                false, null, null
        );
        BusinessHour businessHour7 = new BusinessHour(
                store, null,null, DayOfWeek.SATURDAY, false,
                false, null, null
        );
        businessHourRepository.save(businessHour1);
        businessHourRepository.save(businessHour2);
        businessHourRepository.save(businessHour3);
        businessHourRepository.save(businessHour4);
        businessHourRepository.save(businessHour5);
        businessHourRepository.save(businessHour6);
        businessHourRepository.save(businessHour7);

        String url = "/api/stores/" + store.getStoreId() + "/business-hours";

        //when, then
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(7)))
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'MONDAY' && @.isOpen == true &&@.startTime == '09:00' && @.endTime == '16:00')]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'TUESDAY' && @.isOpen == true && @.startTime == '09:00' && @.endTime == '16:00' )]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'WEDNESDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'THURSDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'FRIDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'SATURDAY' && @.isOpen == false)]").exists())
                .andExpect(jsonPath("$.data[?(@.dayOfWeek == 'SUNDAY' && @.isOpen == false)]").exists())

                .andDo(MockMvcResultHandlers.print());
    }
}
