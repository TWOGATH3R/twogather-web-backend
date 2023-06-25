package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.twogather.twogatherwebbackend.auth.AuthMessage.INVALID_TOKEN;
import static com.twogather.twogatherwebbackend.auth.AuthMessage.NO_SUCH_MEMBER;
import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.*;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;
import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static org.hamcrest.Matchers.*;

public class BusinessHourAcceptanceTest extends AcceptanceTest{

    @BeforeEach
    public void init(){
        super.setUp();
        registerOwner();
        registerOnlyStore();
        approveStore();
        url = "/api/stores/" + storeId + "/business-hours";
    }

    private String url;

    @Test
    @DisplayName("save: 열린 날만 요청해도 영업안하는요일까지 포함해서 응답으로 줘야한다")
    public void whenOnlyOpenDaysProvided_thenResponseIncludesClosedDays(){
        //given
        saveBusinessHour()
                .statusCode(HttpStatus.CREATED.value())
                .body("data", hasSize(7))
                .body("data.find { it.dayOfWeek == 'MONDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'TUESDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'WEDNESDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'THURSDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'FRIDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SATURDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SUNDAY' && it.isOpen == false }", notNullValue());
    }

    @Test
    @DisplayName("save: 중복으로 save를 요청하는 경우 throw exception: update를 수행해야합니다")
    public void whenDuplicateSave_thenThrowException(){
        //given

        //when
        saveBusinessHour()
                .statusCode(HttpStatus.CREATED.value());
        //then
        saveBusinessHour()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }


    @Test
    @DisplayName("save: 탈퇴한 회원의 경우 영업시간을 저장할때 401 throw exception")
    public void whenLeavedUserRequest_thenThrowException(){
        // given
        //when
        leaveOwner();
        //then
        saveBusinessHour()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

    }

    @Test
    @DisplayName("save: 탈퇴한 회원은 아니지만 삭제된 가게의 경우에도 throw exception")
    public void whenDeletedStoreRequest_thenThrowException()  {
        // given
        //when
        removeStore();
        //then
        saveBusinessHour()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo(NO_SUCH_STORE.getMessage()));

    }

    @Test
    @DisplayName("save: 영업시작시간이 영업종료시간보다 나중이라면 exception을 throw해야한다")
    public void whenStartTimeIsLaterThanEndTime_thenThrowException() {
        //given
        BusinessHourSaveUpdateListRequest saveRequest = createStartTimeIsLaterThanEndTimeBusinessHourRequest(storeId);

        //when, then
        saveBusinessHour(saveRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(START_TIME_MUST_BE_BEFORE_END_TIME.getMessage()));

    }

    @Test
    @DisplayName("save: 만약 hasbreaktime을 true로 설정해놨는데 starttime이나 endtime중에 하나를 null넣으면 exception throw")
    public void whenValidateBreakTimeNull_thenThrowException() {
        //given
        BusinessHourSaveUpdateListRequest saveRequest = createNullTimeBusinessHourRequest(storeId);
        //when, then
        saveBusinessHour(saveRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(MUST_HAVE_START_TIME_AND_END_TIME.getMessage()));

    }

    @Test
    @DisplayName("save: 만약 isopen을 true로 설정해놨는데 starttime이나 endtime중에 하나를 null넣으면 exception throw")
    public void whenValidateOpenEndTimeNull_thenThrowException() {
        //given
        BusinessHourSaveUpdateListRequest request = createInvalidTimeBusinessHourRequest(storeId);
        //when, then
        saveBusinessHour(request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(MUST_HAVE_START_TIME_AND_END_TIME.getMessage()));

    }


    @Test
    @DisplayName("update: 요청한 날에 대해서만 UPDATE 수행한다. 요청하지 않은 날에 대해서는 삭제나 수정을 진행하지 않는다")
    public void whenOnlyUpdateDaysProvided_thenReviseProvidedDays() {
        //given
        //when
        saveBusinessHour();
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
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo(NO_SUCH_STORE.getMessage()));

    }

    @Test
    @DisplayName("update: 탈퇴한 회원이 업데이트를 요청할 경우 throw exception")
    public void whenLeavedStoreOwnerUpdateRequest_thenThrowException() {
        //given
        BusinessHourSaveUpdateListRequest request = createBusinessHourRequest(storeId);

        String leaveMemberUrl = OWNER_URL+"/" + memberResponse.getMemberId();
        doDelete(leaveMemberUrl,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken());

        //when, then
        doPut(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), request)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo(NO_SUCH_MEMBER));

    }

    //get
    @Test
    @DisplayName("get: 가게 id에 해당하는 영업시간 7개 정보를 모두 가져온다")
    public void whenGetBusinessHourByStoreId_thenReturnAllDayOfWeekBusinessHour() {
        //given
        //when
        saveBusinessHour();
        //then
        doGet(url,null,null)
                .body("data", hasSize(7))
                .body("data.find { it.dayOfWeek == 'MONDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'THURSDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00'}", notNullValue())
                .body("data.find { it.dayOfWeek == 'WEDNESDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'TUESDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'FRIDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SATURDAY' && it.isOpen == false }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SUNDAY' && it.isOpen == false }", notNullValue());

    }

    @Test
    @DisplayName("save: 토큰 없이 요청 시 throw exception")
    public void whenNoTokenRequest_thenThrowException() {
        String url = "/api/stores/" + 1 + "/business-hours";
        //when, then
        doPost(url,null,null, createBusinessHourRequest(1l))
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo(INVALID_TOKEN));
    }

    private ValidatableResponse saveBusinessHour(){
        BusinessHourSaveUpdateListRequest saveRequest = createBusinessHourRequest(storeId);
        return doPost(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), saveRequest);
    }

    private ValidatableResponse saveBusinessHour(BusinessHourSaveUpdateListRequest request){
        return doPost(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), request);
    }




}
