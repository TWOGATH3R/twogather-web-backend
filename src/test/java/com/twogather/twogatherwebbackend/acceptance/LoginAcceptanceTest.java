package com.twogather.twogatherwebbackend.acceptance;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.INVALID_ID_AND_PASSWORD;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Base64;


public class LoginAcceptanceTest extends AcceptanceTest{

    @BeforeEach
    public void init() {
        registerOwner();
        registerConsumer();
    }

    @Test
    public void createSecretKey512(){
        final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }

    @Test
    @DisplayName("owner 로그인 성공 시, 토큰에 owner 권한 정보와 memberId가 들어있는지 확인")
    public void WhenOwnerLogin_ThenSuccess() throws Exception {
        given()
                .contentType(ContentType.JSON)
                .body(OWNER_LOGIN_REQUEST)
                .post("/api/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .header(constants.REFRESH_TOKEN_HEADER, notNullValue())
                .header(constants.ACCESS_TOKEN_HEADER, notNullValue());

    }

    @Test
    @DisplayName("consumer 로그인 성공 시, 토큰에 consumer 권한 정보와 memberId가 들어있는지 확인")
    public void WhenConsumerLogin_ThenSuccess() {
        given()
                .contentType(ContentType.JSON)
                .body(CONSUMER_LOGIN_REQUEST)
                .post("/api/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .header(constants.REFRESH_TOKEN_HEADER, notNullValue())
                .header(constants.ACCESS_TOKEN_HEADER, notNullValue());

    }
    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도 시, 오류 메시지 반환 확인")
    public void WhenAttemptToLoginWithInvalidPassword_ThenUnauthorizedException() {
        given()
                .contentType(ContentType.JSON)
                .body(OWNER_INVALID_LOGIN_REQUEST)
                .post("/api/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo("아이디나 비밀번호가 틀렸습니다"));
    }
    @Test
    @DisplayName("잘못된 아이디로 로그인 시도 시, 오류 메시지 반환 확인")
    public void WhenAttemptToLoginWithInvalidId_ThenUnauthorizedException() {
        // Given
        LoginRequest invalidLoginRequest = new LoginRequest( "dsa@amer.com", "username1","sss313213");

        // When
        given()
                .contentType(ContentType.JSON)
                .body(invalidLoginRequest)
                .post("/api/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo("아이디나 비밀번호가 틀렸습니다"));

    }

    @Test
    @DisplayName("탈퇴한 아이디로 로그인 시도 시, 오류 메시지 반환 확인")
    public void WhenAttemptLoginDeletedId_ThenUnauthorizedException() {
        // Given
        leaveOwner();
        // When then
        given()
                .contentType(ContentType.JSON)
                .body(OWNER_LOGIN_REQUEST)
                .post("/api/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", equalTo("아이디나 비밀번호가 틀렸습니다"));



    }

}
