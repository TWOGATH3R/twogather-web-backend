package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import com.twogather.twogatherwebbackend.repository.StoreRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.controller.DocumentUtils.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class BusinessHourControllerTest extends AcceptanceTest{

    @Autowired
    private StoreRepository storeRepository;

    @Test
    @DisplayName("save: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void save_WhenValidRequest_ShouldResponse(){
        final BusinessHourSaveRequest request = returnBusinessHourSaveRequest();
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("business-hour/save", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/business-hour")
                .then().log().all().extract();
    }
    @Test
    @DisplayName("save: 영업시작시간이 영업마감 시간보다 늦으면 예외를 발생시킨다")
    public void save_InvalidTime_Exception(){
        final BusinessHourSaveRequest request = returnInvalidBusinessHourSaveRequest();
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("business-hour/save-exception", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/business-hour")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value()) // 예외가 발생하면 BAD_REQUEST(400) 상태 코드가 반환되어야 합니다.
                .body("message", equalTo(BusinessHourException.BusinessHourErrorCode.INVALID_TIME.getMessage()));
    }
    @Test
    @DisplayName("update: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void update_WhenValidRequest_ShouldResponse() {
        //given
        final Long id = 1l;
        //when
        BusinessHourSaveRequest request = saveStoreAndBusinessHour();
        //then
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("business-hour/update", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put("/api/business-hour/" + id)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("update: 영업시작시간이 영업마감 시간보다 늦으면 예외를 발생시킨다")
    public void update_InvalidTime_Exception(){
        //given
        final Long id = 1l;
        //when
        BusinessHourSaveRequest request = saveStoreAndBusinessHour();
        //then
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("business-hour/update", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put("/api/business-hour/" + id)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value()) // 예외가 발생하면 BAD_REQUEST(400) 상태 코드가 반환되어야 합니다.
                .body("message", equalTo(BusinessHourException.BusinessHourErrorCode.INVALID_TIME.getMessage()));
    }

    @Test
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
                .filter(document("business-hour/update", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().delete("/api/business-hour/" + id)
                .then().log().all();

    }
    private BusinessHourSaveRequest saveStoreAndBusinessHour(){
        Long storeId = saveStore();
        final BusinessHourSaveRequest request = returnInvalidBusinessHourSaveRequest(storeId);
        saveBusinessHour();
        return request;
    }

    private Long saveStore(){
        return storeRepository.save(new Store()).getStoreId();
    }
    private void saveBusinessHour(){
        BusinessHourSaveRequest request = returnBusinessHourSaveRequest();
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/business-hour")
                .then().log().all().extract();
    }
    private BusinessHourSaveRequest returnBusinessHourSaveRequest(){
        return new BusinessHourSaveRequest(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN);
    }
    private BusinessHourSaveRequest returnInvalidBusinessHourSaveRequest(Long storeId){
        return new BusinessHourSaveRequest(storeId, END_TIME, START_TIME, DAY_OF_WEEK, IS_OPEN);
    }
    private BusinessHourSaveRequest returnInvalidBusinessHourSaveRequest(){
        return new BusinessHourSaveRequest(STORE_ID, END_TIME, START_TIME, DAY_OF_WEEK, IS_OPEN);
    }
}
