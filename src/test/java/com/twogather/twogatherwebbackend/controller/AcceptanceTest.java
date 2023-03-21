package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.UTF8EncodingFilter;
import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveRequest;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.twogather.twogatherwebbackend.controller.DocumentUtils.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ExtendWith({SpringExtension.class})
@ExtendWith({RestDocumentationExtension.class})
@ActiveProfiles("test")
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        RequestSpecification spec = new RequestSpecBuilder()
                .addFilter(new UTF8EncodingFilter())
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
        setRequestSpecification(spec);
    }

    ExtractableResponse<Response> saveOwner(final StoreOwnerSaveRequest ownerSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json; charset=UTF-8")
                .filter(document("owner/save", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(ownerSaveRequest)
                .when().post("/api/owners")
                .then().log().all().extract();
    }
}
