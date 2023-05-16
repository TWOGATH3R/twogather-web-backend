package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.controller.ControllerTest;

import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class BusinessHourTest extends ControllerTest {

    /*
    @Test1
    @DisplayName("save: 유효한 businessHour 요청이 왔을때 businessHour 정보를 반환한다")
    public void WhenValidRequest_ShouldResponse(){
        //then
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .header(AUTH, token)
                .filter(document("business-hours/save", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(returnSaveBusinessHour(storeId))
                .when().post("/api/business-hours")
                .then().log().all().extract();
    }
    @Test1
    @DisplayName("save: 영업시작시간이 영업마감 시간보다 늦으면 예외를 발생시킨다")
    public void save_WhenInvalidTime_shouldException(){
        final BusinessHourSaveRequest request = INVALID_BUSINESS_HOUR_SAVE_REQUEST;
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("business-hours/save-exception", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/business-hours")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value()) // 예외가 발생하면 BAD_REQUEST(400) 상태 코드가 반환되어야 합니다.
                .body("message", equalTo(INVALID_TIME.getMessage()));
    }
    @Test1
    @DisplayName("update: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void update_WhenValidRequest_ShouldResponse() {
        //given
        final Long id = storeId;
        //when
        BusinessHourSaveRequest request = saveStoreAndBusinessHour();
        //then
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("business-hours/update", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put("/api/business-hours/" + id)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test1
    @DisplayName("update: 영업시작시간이 영업마감 시간보다 늦으면 예외를 발생시킨다")
    public void update_InvalidTime_Exception(){
        //given
        final Long id = storeId;
        //when
        BusinessHourSaveRequest request = saveStoreAndBusinessHour();
        //then
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("business-hours/update", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put("/api/business-hours/" + id)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value()) // 예외가 발생하면 BAD_REQUEST(400) 상태 코드가 반환되어야 합니다.
                .body("message", equalTo(INVALID_TIME.getMessage()));
    }

    @Test1
    @DisplayName("delete: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void delete_WhenValidRequest_ShouldResponse(){
        //given
        final Long id = 1l;
        //when
        BusinessHourSaveRequest request = saveStoreAndBusinessHour();
        //then
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("business-hours/update", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().delete("/api/business-hours/" + id)
                .then().log().all();

    }
    private BusinessHourSaveRequest saveStoreAndBusinessHour(){
        ownerSave();
        String token = ownerLoginReturnToken();
        Long storeId = storeSave(token);
        final BusinessHourSaveRequest request = returnSaveBusinessHour(storeId);
        saveBusinessHour(request);
        return request;
    }

    private void saveBusinessHour(final BusinessHourSaveRequest request){
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/business-hours")
                .then().log().all().extract();
    }
    private static BusinessHourSaveRequest returnSaveBusinessHour(Long storeId){
        return new BusinessHourSaveRequest(storeId, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);
    }*/
}
