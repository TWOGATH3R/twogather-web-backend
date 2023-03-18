package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.LoginRequest;
import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.twogather.twogatherwebbackend.controller.DocumentUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
public class AuthControllerTest extends AcceptanceTest{
    private static final String EMAIL = "asd@naver.com";
    private static final String PASSWORD = "asd@asd@@123";
    private static final String NAME = "루터";
    private static final String PHONE = "01012341234";
    private static final String BUSINESS_NAME = "루터";
    private static final String BUSINESS_NUMBER = "0000000000";
    private static final String BUSINESS_START_DATE = "20200101";


    @DisplayName("유효한 정보의 로그인 요청이 오면 200 ok를 응답한다.")
    @Test
    void login() {
        // given
        saveOwner(returnOwnerRequest());

        // when
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        ExtractableResponse<Response> response = login(loginRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("유효하지 않은 정보의 로그인 요청이 오면 400 Bad Request를 응답한다.")
    @Test
    void loginWithInvalidInformation() {
        // given
        saveOwner(returnOwnerRequest());

        // when
        LoginRequest loginRequest = new LoginRequest(EMAIL, "WrongPassword1234");
        ExtractableResponse<Response> response = login(loginRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> login(final LoginRequest loginRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("owner/login", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/login/token")
                .then().log().all().extract();
    }

    private StoreOwnerSaveRequest returnOwnerRequest(){
        return new StoreOwnerSaveRequest(
                EMAIL,PASSWORD,NAME, PHONE,BUSINESS_NUMBER,BUSINESS_NAME,BUSINESS_START_DATE
        );
    }
}
