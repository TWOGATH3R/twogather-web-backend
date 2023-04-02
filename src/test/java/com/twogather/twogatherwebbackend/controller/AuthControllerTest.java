package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.member.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveRequest;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.controller.DocumentUtils.*;
import static com.twogather.twogatherwebbackend.exception.AuthException.AuthExceptionErrorCode.ACCESS_DENIED;
import static com.twogather.twogatherwebbackend.exception.AuthException.AuthExceptionErrorCode.UNAUTHORIZED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class AuthControllerTest extends AcceptanceTest{
    static final String AUTH = "Authorization";

    @DisplayName("가게주인: 유효한 정보의 로그인 요청이 오면 200 ok를 응답한다.")
    @Test
    void whenLoginOwnerThenSuccess() {
        // given
        saveOwner(OWNER_SAVE_REQUEST);

        // when
        ExtractableResponse<Response> response = returnLoginOwner();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("가게주인이 로그인 이후 가게 주인만 접근할 수 있는 자원에 접근")
    @Test
    void whenLoginWithOwnerResourceThenSuccess(){
        //given
        String ownerToken = returnOwnerToken();
        //when
        ExtractableResponse<Response> response = RestAssured
                .given(getRequestSpecification()).log().all()
                .header(new Header(AUTH, ownerToken))
                .accept("application/json; charset=UTF-8")
                .filter(document("owner/login/store-owner-only", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/owners/" + OWNER_EMAIL)
                .then().log().all().extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("소비자가 로그인 이후 가게 주인만 접근할 수 있는 자원에 접근할때 예외발생")
    @Test
    void afterLoginWithConsumerAccessResourceThenException(){
        //given
        String consumerToken = returnConsumerToken();
        //when, then
        RestAssured
                .given(getRequestSpecification()).log().all()
                .header(new Header(AUTH, consumerToken))
                .accept("application/json; charset=UTF-8")
                .filter(document("owner/login/store-owner-only-exception", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/owners/" + CONSUMER_EMAIL)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", equalTo(ACCESS_DENIED.getMessage()));
    }

    @DisplayName("가게주인이 로그인 이후 소비자만 접근할 수 있는 자원에 접근할때 예외발생")
    @Test
    void afterLoginWithOwnerAccessResourceThenException(){
        //given
        String ownerToken = returnOwnerToken();
        //when, then
        RestAssured
                .given(getRequestSpecification()).log().all()
                .header(new Header(AUTH, ownerToken))
                .accept("application/json; charset=UTF-8")
                .filter(document("consumer/login/consumer-only-exception", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/consumers/" + OWNER_EMAIL)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", equalTo(ACCESS_DENIED.getMessage()));
    }

    @DisplayName("유효하지 않은 정보의 로그인 요청이 오면 400 Bad Request를 응답한다.")
    @Test
    void afterInvalidLoginThenException() {
        // given
        saveOwner(OWNER_SAVE_REQUEST);

        // when
        ExtractableResponse<Response> response = returnWrongPasswordLoginOwner();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("로그아웃 이후에는 권한 없는곳에 접근할 수 없다")
    @Test
    void afterLogoutThenNotAccess(){
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("owner/login/unauthorized", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/owners/" + OWNER_EMAIL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo(UNAUTHORIZED.getMessage()));
    }

    @DisplayName("로그인 시 memberId를 반환한다")
    @Test
    void afterLoginThenReturnMemberId(){

    }

    private ExtractableResponse<Response> returnLoginOwner() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("owner/login", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(OWNER_LOGIN_REQUEST)
                .when().post("/api/login/token")
                .then().log().all().extract();
    }
    private ExtractableResponse<Response> returnWrongPasswordLoginOwner() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("owner/login", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(OWNER_INVALID_LOGIN_REQUEST)
                .when().post("/api/login/token")
                .then().log().all().extract();
    }

    private String returnOwnerToken(){
        saveOwner(OWNER_SAVE_REQUEST);

        ExtractableResponse<Response> response = loginOwner();

        return response.header(AUTH);
    }
    private String returnConsumerToken() {
        saveConsumer(CONSUMER_SAVE_REQUEST);

        ExtractableResponse<Response> response = loginConsumer();

        return response.header(AUTH);
    }
    ExtractableResponse<Response> saveOwner(final StoreOwnerSaveRequest ownerSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")//클라 -> 서버에게 요청하는 데이터가 json이라는 것을 명시하는 것
                .filter(document("owner/save", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ownerSaveRequest)
                .when().post("/api/owners")
                .then().log().all().extract();
    }
    ExtractableResponse<Response> saveConsumer(final ConsumerSaveRequest consumerSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("consumer/save1", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(consumerSaveRequest)
                .when().post("/api/consumers")
                .then().log().all().extract();
    }
    ExtractableResponse<Response> loginConsumer(){
        LoginRequest loginRequest = new LoginRequest(CONSUMER_EMAIL, CONSUMER_PASSWORD);
        ExtractableResponse<Response> response = RestAssured
                .given(getRequestSpecification()).log().all()
                .filter(document("consumer/login", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/login/token")
                .then().log().all().extract();
        return response;
    }
    ExtractableResponse<Response> loginOwner(){
        LoginRequest loginRequest = OWNER_LOGIN_REQUEST;
        ExtractableResponse<Response> response = RestAssured
                .given(getRequestSpecification()).log().all()
                .filter(document("owner/login", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/login/token")
                .then().log().all().extract();
        return response;
    }
}
