package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.Tokens;
import com.twogather.twogatherwebbackend.controller.BusinessHourController;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreResponse;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.*;
import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.UNAUTHORIZED;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class BusinessHourAcceptanceTest extends AcceptanceTest{

    @Autowired
    private ConsumerRepository consumerRepository;

    @Test
    @DisplayName("save: 열린 날만 요청해도 영업안하는요일까지 포함해서 응답으로 줘야한다")
    public void whenOnlyOpenDaysProvided_thenResponseIncludesClosedDays(){
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);

        Long storeId = storeResponse.getStoreId();
        BusinessHourController.BusinessHourSaveUpdateListRequest request = createBusinessHourRequest(storeId);

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        given().contentType("application/json")
                .body(request)
                .header(constants.ACCESS_TOKEN_HEADER, myToken.getAccessToken())
                .header(constants.REFRESH_TOKEN_HEADER, myToken.getRefreshToken())
                .when()
                .post(url)
                .then()
                .log().all()
                .statusCode(201)
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
    @DisplayName("save: 탈퇴한 회원의 경우 throw exception")
    public void whenLeavedUserRequest_thenThrowException(){
        // given
        MemberResponse memberResponse =
                doPost(
                        OWNER_URL,
                        OWNER_SAVE_UPDATE_REQUEST,
                        MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);
        validatorWillPass();

        StoreResponse storeResponse = doPost(STORE_URL, myToken.getRefreshToken(), myToken.getAccessToken(), STORE_SAVE_REQUEST, StoreResponse.class);
        Long storeId = storeResponse.getStoreId();

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        BusinessHourController.BusinessHourSaveUpdateListRequest request = createBusinessHourRequest(storeId);

        String leaveMemberUrl = OWNER_URL+"/" + memberResponse.getMemberId();
        doDelete(leaveMemberUrl, myToken.getRefreshToken(), myToken.getAccessToken());

        String url = "/api/stores/" + storeId + "/business-hours";
        //when, then
        doPost(url, myToken.getRefreshToken(), myToken.getAccessToken(), request)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo(UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("save: 탈퇴한 회원은 아니지만 삭제된 가게의 경우 throw exception")
    public void whenDeletedStoreRequest_thenThrowException() throws Exception {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens ownerToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        ownerToken.getRefreshToken(),
                        ownerToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);

        Long storeId = storeResponse.getStoreId();
        BusinessHourController.BusinessHourSaveUpdateListRequest request = createBusinessHourRequest(storeId);

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        String deleteStoreUrl = STORE_URL+"/" + storeId;
        doDelete(deleteStoreUrl, ownerToken.getRefreshToken(), ownerToken.getAccessToken());

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        doPost(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), request)
                .statusCode(HttpStatus.NOT_FOUND.value());

    }
    @Test
    @DisplayName("save: 모든요일에 대해 열린날을 요청하면 응답으로 닫힌날을 제공하면안된다")
    public void whenAllOpenDaysProvided_thenResponseIncludesNoClosedDays() {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);


        Long storeId = storeResponse.getStoreId();
        BusinessHourController.BusinessHourSaveUpdateListRequest request = createBusinessHourRequestWithAllDayOfWeek(storeId);

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        doPost(url,  myToken.getRefreshToken(), myToken.getAccessToken(), request)
                .statusCode(HttpStatus.CREATED.value())
                .body("data", hasSize(7))
                .body("data.findAll { it.dayOfWeek == 'MONDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", is(not(empty())))
                .body("data.findAll { it.dayOfWeek == 'TUESDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", is(not(empty())))
                .body("data.findAll { it.dayOfWeek == 'WEDNESDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", is(not(empty())))
                .body("data.findAll { it.dayOfWeek == 'THURSDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", is(not(empty())))
                .body("data.findAll { it.dayOfWeek == 'FRIDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", is(not(empty())))
                .body("data.findAll { it.dayOfWeek == 'SATURDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", is(not(empty())))
                .body("data.findAll { it.dayOfWeek == 'SUNDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", is(not(empty())));

    }

    @Test
    @DisplayName("save: 영업시작시간이 영업종료시간보다 나중이라면 exception을 throw해야한다")
    public void whenStartTimeIsLaterThanEndTime_thenThrowException() throws Exception {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);

        Long storeId = storeResponse.getStoreId();

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        BusinessHourController.BusinessHourSaveUpdateListRequest request = createStartTimeIsLaterThanEndTimeBusinessHourRequest(storeId);
        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        doPost(url, myToken.getRefreshToken(), myToken.getAccessToken(), request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(START_TIME_MUST_BE_BEFORE_END_TIME.getMessage()));

    }
    @Test
    @DisplayName("save: 브레이크시작시간이 브레이크종료시간보다 나중이라면 exception을 throw해야한다")
    public void whenBreakStartTimeIsLaterThanEndTime_thenThrowException() throws Exception {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);

        Long storeId = storeResponse.getStoreId();

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        BusinessHourController.BusinessHourSaveUpdateListRequest request = createStartTimeIsLaterThanEndTimeBusinessHourRequest(storeId);
        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        doPost(url, myToken.getRefreshToken(), myToken.getAccessToken(), request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(START_TIME_MUST_BE_BEFORE_END_TIME.getMessage()));

    }

    @Test
    @DisplayName("save: 만약 hasbreaktime을 true로 설정해놨는데 starttime이나 endtime중에 하나를 null넣으면 exception throw")
    public void whenValidateBreakTimeNull_thenThrowException() throws Exception {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);

        Long storeId = storeResponse.getStoreId();

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        BusinessHourController.BusinessHourSaveUpdateListRequest request = createNullTimeBusinessHourRequest(storeId);
        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        doPost(url, myToken.getRefreshToken(), myToken.getAccessToken(), request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(MUST_HAVE_START_TIME_AND_END_TIME.getMessage()));

    }

    @Test
    @DisplayName("save: 만약 isopen을 true로 설정해놨는데 starttime이나 endtime중에 하나를 null넣으면 exception throw")
    public void whenValidateOpenEndTimeNull_thenThrowException() {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);

        Long storeId = storeResponse.getStoreId();

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        BusinessHourController.BusinessHourSaveUpdateListRequest request = createInvalidTimeBusinessHourRequest(storeId);
        String url = "/api/stores/" + storeId + "/business-hours";

        //when, then
        doPost(url, myToken.getRefreshToken(), myToken.getAccessToken(), request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(MUST_HAVE_START_TIME_AND_END_TIME.getMessage()));

    }

    @Test
    @DisplayName("update: 요청한 날에 대해서만 UPDATE 수행한다. 요청하지 않은 날에 대해서는 삭제나 수정을 진행하지 않는다")
    public void whenOnlyUpdateDaysProvided_thenReviseProvidedDays() {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);


        Long storeId = storeResponse.getStoreId();
        String url = "/api/stores/" + storeId + "/business-hours";

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        BusinessHourController.BusinessHourSaveUpdateListRequest saveRequest = createBusinessHourRequestWithAllDayOfWeek(storeId);
        doPost(url, myToken.getRefreshToken(), myToken.getAccessToken(), saveRequest)
                .statusCode(HttpStatus.CREATED.value());

        //when, then
        BusinessHourController.BusinessHourSaveUpdateListRequest updateRequest = createUpdateBusinessHourRequest(storeId);
        doPut(url, myToken.getRefreshToken(), myToken.getAccessToken(), updateRequest)
                .statusCode(HttpStatus.OK.value())
                .body("data", hasSize(7))
                .body("data.find { it.dayOfWeek == 'MONDAY' && it.isOpen == true && it.startTime == '11:00' && it.endTime == '17:00' && it.hasBreakTime == true && it.breakStartTime == '12:00' && it.breakEndTime == '13:00'}", notNullValue())
                .body("data.find { it.dayOfWeek == 'THURSDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'WEDNESDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'THURSDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SATURDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'FRIDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", notNullValue())
                .body("data.find { it.dayOfWeek == 'SUNDAY' && it.isOpen == true && it.startTime == '09:00' && it.endTime == '16:00' }", notNullValue());

    }

    @Test
    @DisplayName("update: 업데이트를 요청한 결과 해당 가게에 중복되는 요일이 생겨버린다면 예외 throw")
    public void whenUpdateDuplicatedDayOfWeek_thenThrowsException() {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);


        Long storeId = storeResponse.getStoreId();
        String url = "/api/stores/" + storeId + "/business-hours";

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        BusinessHourController.BusinessHourSaveUpdateListRequest saveRequest = createBusinessHourRequestWithAllDayOfWeek(storeId);
        doPost(url, myToken.getRefreshToken(), myToken.getAccessToken(), saveRequest)
                .statusCode(HttpStatus.CREATED.value());

        BusinessHourController.BusinessHourSaveUpdateListRequest request = createDuplicatedDayOfWeekBusinessHourRequest(storeId);

        //when, then
        doPut(url, myToken.getRefreshToken(), myToken.getAccessToken(), request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(DUPLICATE_DAY_OF_WEEK.getMessage()));

    }

    @Test
    @DisplayName("update: 삭제된 가게에 대해 업데이트를 요청할 경우 throw exception")
    public void whenDeletedStoreUpdateRequest_thenThrowException() {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);


        Long storeId = storeResponse.getStoreId();
        String url = "/api/stores/" + storeId + "/business-hours";
        BusinessHourController.BusinessHourSaveUpdateListRequest saveRequest = createBusinessHourRequestWithAllDayOfWeek(storeId);

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        doPost( url,
                myToken.getRefreshToken(),
                myToken.getAccessToken(),
                saveRequest)
                    .statusCode(HttpStatus.CREATED.value());

        doDelete(STORE_URL + "/" + storeId, myToken.getRefreshToken(), myToken.getAccessToken());

        BusinessHourController.BusinessHourSaveUpdateListRequest updateRequest = createDuplicatedDayOfWeekBusinessHourRequest(storeId);

        //when, then
        doPut(url, myToken.getRefreshToken(), myToken.getAccessToken(), updateRequest)
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @Test
    @DisplayName("update: 탈퇴한 회원이 업데이트를 요청할 경우 throw exception")
    public void whenLeavedStoreOwnerUpdateRequest_thenThrowException() {
        MemberResponse memberResponse =
                doPost(
                        OWNER_URL,
                        OWNER_SAVE_UPDATE_REQUEST,
                        MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);
        validatorWillPass();

        StoreResponse storeResponse = doPost(STORE_URL, myToken.getRefreshToken(), myToken.getAccessToken(), STORE_SAVE_REQUEST, StoreResponse.class);
        Long storeId = storeResponse.getStoreId();

        BusinessHourController.BusinessHourSaveUpdateListRequest request = createBusinessHourRequest(storeId);

        String leaveMemberUrl = OWNER_URL+"/" + memberResponse.getMemberId();
        doDelete(leaveMemberUrl, myToken.getRefreshToken(), myToken.getAccessToken());

        String url = "/api/stores/" + storeId + "/business-hours";
        //when, then
        doPut(url, myToken.getRefreshToken(), myToken.getAccessToken(), request)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo(UNAUTHORIZED.getMessage()));

    }

    //get
    @Test
    @DisplayName("get: 가게 id에 해당하는 영업시간 7개 정보를 모두 가져온다")
    public void whenGetBusinessHourByStoreId_thenReturnAllDayOfWeekBusinessHour() {
        //given
        doPost(
                        OWNER_URL,
                        OWNER_SAVE_UPDATE_REQUEST,
                        MemberResponse.class);

        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);
        validatorWillPass();

        StoreResponse storeResponse = doPost(STORE_URL, myToken.getRefreshToken(), myToken.getAccessToken(), STORE_SAVE_REQUEST, StoreResponse.class);
        Long storeId = storeResponse.getStoreId();

        String url = "/api/stores/" + storeId + "/business-hours";

        consumerRepository.save(ADMIN);
        Tokens adminToken = doLogin(LOGIN_URL, ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());

        BusinessHourController.BusinessHourSaveUpdateListRequest request = createBusinessHourRequest(storeId);
        doPost(
                url,
                myToken.getRefreshToken(),
                myToken.getAccessToken(),
                request
        )
                .statusCode(HttpStatus.CREATED.value());

        //when, then
        doGet(url)
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
    @DisplayName("save: 토큰 없이 요청 시 throw exception")
    public void whenNoTokenRequest_thenThrowException() {
        String url = "/api/stores/" + 1 + "/business-hours";
        //when, then
        doPost(url, createBusinessHourRequest(1l))
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo(UNAUTHORIZED.getMessage()));
    }

}
