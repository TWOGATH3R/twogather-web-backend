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
        doPut(url,null,null, createBusinessHourRequest(1l))
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo(INVALID_TOKEN));
    }

    private ValidatableResponse saveBusinessHour(){
        BusinessHourSaveUpdateListRequest saveRequest = createBusinessHourRequest(storeId);
        return doPut(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), saveRequest);
    }

    private ValidatableResponse saveBusinessHour(BusinessHourSaveUpdateListRequest request){
        return doPut(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), request);
    }




}
