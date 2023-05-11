package com.twogather.twogatherwebbackend.acceptance;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.auth.JwtProperties;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.exception.CustomAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.INVALID_ID_AND_PASSWORD;
import static javax.crypto.Cipher.SECRET_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;


@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createSecretKey512(){
// Generate a 512-bit key
        final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }

    @Test
    @DisplayName("owner 로그인 성공 시, 토큰에 owner 권한 정보와 memberId가 들어있는지 확인")
    public void WhenOwnerLogin_ThenSuccess() throws Exception {

        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OWNER_LOGIN_REQUEST)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists(JwtProperties.HEADER_STRING))
                .andReturn();

        // Then
        String originToken = mvcResult.getResponse().getHeader(JwtProperties.HEADER_STRING);
        String token = originToken.replace(JwtProperties.TOKEN_PREFIX, "");

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(token);

        assertThat(decodedJWT.getClaim("id").asLong()).isEqualTo(STORE_OWNER.getMemberId());
        assertThat(decodedJWT.getClaim("role").asString()).isEqualTo(STORE_OWNER.getAuthenticationType().authority());
        assertThat(decodedJWT.getClaim("username").asString()).isEqualTo(STORE_OWNER.getEmail());
    }

    @Test
    @DisplayName("consumer 로그인 성공 시, 토큰에 consumer 권한 정보와 memberId가 들어있는지 확인")
    public void WhenConsumerLogin_ThenSuccess() throws Exception {

        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CONSUMER_LOGIN_REQUEST)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists(JwtProperties.HEADER_STRING))
                .andReturn();

        // Then
        String originToken = mvcResult.getResponse().getHeader(JwtProperties.HEADER_STRING);
        String token = originToken.replace(JwtProperties.TOKEN_PREFIX, "");

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                .build()
                .verify(token);

        assertThat(decodedJWT.getClaim("id").asLong()).isEqualTo(CONSUMER.getMemberId());
        assertThat(decodedJWT.getClaim("role").asString()).isEqualTo(CONSUMER.getAuthenticationType().authority());
        assertThat(decodedJWT.getClaim("username").asString()).isEqualTo(CONSUMER.getEmail());
    }
    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도 시, 오류 메시지 반환 확인")
    public void WhenAttemptToLoginWithInvalidPassword_ThenUnauthorizedException() throws Exception {
        // Given
        LoginRequest invalidLoginRequest = OWNER_INVALID_LOGIN_REQUEST;

        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andDo(print())
                .andReturn();

        // Then
        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        String jsonString = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        String message = jsonNode.get("message").asText();
        assertThat(message).isEqualTo(INVALID_ID_AND_PASSWORD.getMessage());
    }

}
