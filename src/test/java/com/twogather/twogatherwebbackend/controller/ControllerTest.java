package com.twogather.twogatherwebbackend.controller;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.controller.DocumentUtils.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class})
@ActiveProfiles("test")
public class ControllerTest {
    @LocalServerPort
    int port;
    public static String token;
    public static Long storeId;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        RequestSpecification spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
        setRequestSpecification(spec);
    }

    public static Long storeSave(String token){
        ExtractableResponse<Response> response = RestAssured
                .given(getRequestSpecification()).log().all()
                .filter(document("stores/save", getRequestPreprocessor(), getResponsePreprocessor()))
                .header(AUTH, token)
                .accept("application/json; charset=UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(STORE_SAVE_REQUEST)
                .when().post("/api/stores")
                .then().log().all().extract();
        return response.as(StoreController.Response.class).getData().getId();
    }
    public static void ownerSave(){
        RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("owners/save", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(OWNER_SAVE_REQUEST)
                .when().post("/api/owners")
                .then().log().all().extract();
    }
    public static String ownerLoginReturnToken(){
            //login
            ExtractableResponse<Response> loginResponse = RestAssured
                    .given(getRequestSpecification()).log().all()
                    .filter(document("owners/login", getRequestPreprocessor(), getResponsePreprocessor()))
                    .accept("application/json; charset=UTF-8")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(OWNER_LOGIN_REQUEST)
                    .when().post("/api/login/token")
                    .then().log().all().extract();

        return loginResponse.header(AUTH);
    }
}
