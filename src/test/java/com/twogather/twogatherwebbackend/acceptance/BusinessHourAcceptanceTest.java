package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.controller.BusinessHourController;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.*;
import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.UNAUTHORIZED;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BusinessHourAcceptanceTest extends AcceptanceTest{

    @BeforeEach
    public void init(){
        super.setUp();
        registerOwner();
        registerStore();
        approveStore();
        url = "/api/stores/" + storeId + "/business-hours";
    }

    private String url;

    @Test
    @DisplayName("update: 요청한 날에 대해서만 UPDATE 수행한다. 요청하지 않은 날에 대해서는 삭제나 수정을 진행하지 않는다")
    public void whenOnlyUpdateDaysProvided_thenReviseProvidedDays() {
        //given
        //when

        //then
        BusinessHourSaveUpdateListRequest updateRequest = createUpdateBusinessHourRequest(storeId);
        doPut(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), updateRequest)
                .statusCode(HttpStatus.OK.value())
                .body("data", hasSize(7))
                .body("data.find { it.dayOfWeek == 'MONDAY' && it.isOpen == true && it.startTime == '11:00' && it.endTime == '17:00' && it.hasBreakTime == true && it.breakStartTime == '12:00' && it.breakEndTime == '13:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'THURSDAY' && it.isOpen == true && it.startTime == '11:00' && it.endTime == '17:00' && it.hasBreakTime == true && it.breakStartTime == '12:00' && it.breakEndTime == '13:00'  }", notNullValue())
                .body("data.find { it.dayOfWeek == 'WEDNESDAY' && it.isOpen == true && it.startTime == '11:00' && it.endTime == '17:00' && it.hasBreakTime == true && it.breakStartTime == '12:00' && it.breakEndTime == '13:00'   }", notNullValue())
                .body("data.find { it.dayOfWeek == 'THURSDAY' && it.isOpen == true && it.startTime == '11:00' && it.endTime == '17:00' && it.hasBreakTime == true && it.breakStartTime == '12:00' && it.breakEndTime == '13:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SATURDAY' && it.isOpen == true && it.startTime == '11:00' && it.endTime == '17:00' && it.hasBreakTime == true && it.breakStartTime == '12:00' && it.breakEndTime == '13:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'FRIDAY' && it.isOpen == true && it.startTime == '11:00' && it.endTime == '17:00' && it.hasBreakTime == true && it.breakStartTime == '12:00' && it.breakEndTime == '13:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SUNDAY' && it.isOpen == true && it.startTime == '11:00' && it.endTime == '17:00' && it.hasBreakTime == true && it.breakStartTime == '12:00' && it.breakEndTime == '13:00' }", notNullValue());

    }

    @Test
    @DisplayName("update: 업데이트를 요청한 결과 해당 가게에 중복되는 요일이 생겨버린다면 예외 throw")
    public void whenUpdateDuplicatedDayOfWeek_thenThrowsException() {
        //given
        BusinessHourSaveUpdateListRequest updateRequest = createDuplicatedDayOfWeekBusinessHourRequest(storeId);
        //when, then
        doPut(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), updateRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(DUPLICATE_DAY_OF_WEEK.getMessage()));

    }

    @Test
    @DisplayName("update: 삭제된 가게에 대해 업데이트를 요청할 경우 throw exception")
    public void whenDeletedStoreUpdateRequest_thenThrowException() {
        //given
       BusinessHourSaveUpdateListRequest updateRequest = createDuplicatedDayOfWeekBusinessHourRequest(storeId);

        //when
        doDelete(STORE_URL + "/" + storeId, ownerToken.getRefreshToken(), ownerToken.getAccessToken());

        //when, then
        doPut(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), updateRequest)
                .statusCode(HttpStatus.FORBIDDEN.value());

    }

    @Test
    @DisplayName("update: 탈퇴한 회원이 업데이트를 요청할 경우 throw exception")
    public void whenLeavedStoreOwnerUpdateRequest_thenThrowException() {
        //given
        BusinessHourSaveUpdateListRequest request = createBusinessHourRequest(storeId);

        String leaveMemberUrl = OWNER_URL+"/" + memberResponse.getMemberId();
        doDelete(leaveMemberUrl, ownerToken.getRefreshToken(), ownerToken.getAccessToken());

        //when, then
        doPut(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), request)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo(UNAUTHORIZED.getMessage()));

    }

    //get
    @Test
    @DisplayName("get: 가게 id에 해당하는 영업시간 7개 정보를 모두 가져온다")
    public void whenGetBusinessHourByStoreId_thenReturnAllDayOfWeekBusinessHour() {
        //given
        //then
        doGet(url)
                .body("data", hasSize(7))
                .body("data.find { it.dayOfWeek == 'MONDAY' && it.isOpen == true && it.startTime == '11:30' && it.endTime == '20:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'TUESDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'WEDNESDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'THURSDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'FRIDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SATURDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SUNDAY' && it.isOpen == false }", notNullValue());

    }

    @Test
    @DisplayName("save: 토큰 없이 요청 시 throw exception")
    public void whenNoTokenRequest_thenThrowException() {
        String url = "/api/stores/" + 1 + "/business-hours";
        //when, then
        doPost(url, createBusinessHourRequest(1l))
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo(UNAUTHORIZED.getMessage()));
    }


}
