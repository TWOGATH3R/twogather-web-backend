package com.twogather.twogatherwebbackend.acceptance;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.Tokens;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.UNDEFINED_PORT;
import static io.restassured.RestAssured.given;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest
{
    @LocalServerPort
    public int port;
    @Autowired
    protected PrivateConstants constants;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    private BizRegNumberValidator validator;

    @Autowired
    private DatabaseCleanup databaseCleanup;
    @BeforeEach
    void setUp() {
        if(RestAssured.port == UNDEFINED_PORT){
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
    }

    protected <T> T doGet(String path, String refreshToken, String accessToken, Class<T> response) {
        return given()
                .header("Authorization", "Bearer " + refreshToken)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(response);
    }

    protected <T> T doGet(String path, Class<T> response) {
        return given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(response);
    }
    protected <T> ValidatableResponse doGet(String path) {
        return given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }
    protected <T> void doDelete(String path,String refreshToken, String accessToken) {
        given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .when()
                .delete(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }
    protected <T> void doPatch(String path,String refreshToken, String accessToken) {
        given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .when()
                .patch(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }
    protected <T, R> R doPost(String path, String refreshToken, String accessToken, T request, Class<R> responseClass) {

        com.twogather.twogatherwebbackend.dto.Response response = given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(com.twogather.twogatherwebbackend.dto.Response.class);

        return objectMapper.convertValue(response.getData(), responseClass);
    }


    protected <T> ValidatableResponse doPost(String path, String refreshToken, String accessToken, T request) {

        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post(path)
                .then()
                .log().all();

    }
    protected <T> ValidatableResponse doPut(String path, String refreshToken, String accessToken, T request) {

        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .put(path)
                .then()
                .log().all();

    }
    protected <T, R> R doPost(String path, T request, Class<R> responseClass) {
        com.twogather.twogatherwebbackend.dto.Response response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(com.twogather.twogatherwebbackend.dto.Response.class);
        return objectMapper.convertValue(response.getData(), responseClass);
    }

    protected <T> ValidatableResponse doPost(String path, T request) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(path)
                .then()
                .log().all();
    }


    protected <T> Tokens doLogin(String path, T request) {
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        Headers headers = response.headers();

        String accessToken = headers.getValue(constants.ACCESS_TOKEN_HEADER);
        String refreshToken = headers.getValue(constants.REFRESH_TOKEN_HEADER);

        return new Tokens(accessToken, refreshToken);
    }
    protected void validatorWillPass(){
        org.mockito.Mockito.when(validator.validateBizRegNumber(org.mockito.ArgumentMatchers.any(),org.mockito.ArgumentMatchers.any(),org.mockito.ArgumentMatchers.any())).thenReturn(true);
    }

}
