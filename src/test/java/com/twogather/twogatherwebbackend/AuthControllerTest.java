package com.twogather.twogatherwebbackend;

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

    private final String EMAIL = "asd@naver.com";
    private final String PASSWORD = "asd@asd@@123";
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
                EMAIL,PASSWORD,
                "김사업",
                "01012341234",
                "0000000000",
                "김사업이름",
                "20200101"
        );
    }
}
