package com.twogather.twogatherwebbackend.acceptance;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.INVALID_ID_AND_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Base64;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ConsumerRepository consumerRepository;
    @Autowired
    private StoreOwnerRepository storeOwnerRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PrivateConstants constants;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private StoreOwner storeOwner;
    private Consumer consumer;
    @BeforeEach
    public void prepareTestData() {
        Consumer consumer1 = CONSUMER;
        StoreOwner owner1 = STORE_OWNER;
        consumer = consumerRepository.save(consumer1);
        storeOwner = storeOwnerRepository.save(owner1);
        Store store1 = new Store(storeOwner, null,null, "김가네", "전주시 어쩌고 어쩌고", "063-234-1222", true, "");
        storeRepository.save(store1);
    }

    @Test
    public void createSecretKey512(){
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
                .andExpect(MockMvcResultMatchers.header().exists(constants.HEADER_STRING))
                .andReturn();

        // Then
        String originToken = mvcResult.getResponse().getHeader(constants.HEADER_STRING);
        String token = originToken.replace(constants.TOKEN_PREFIX, "");

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(constants.JWT_SECRET))
                .build()
                .verify(token);

        assertThat(decodedJWT.getClaim("id").asLong()).isEqualTo(storeOwner.getMemberId());
        assertThat(decodedJWT.getClaim("role").asString()).isEqualTo(storeOwner.getAuthenticationType().authority());
        assertThat(decodedJWT.getClaim("username").asString()).isEqualTo(storeOwner.getEmail());
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
                .andExpect(MockMvcResultMatchers.header().exists(constants.HEADER_STRING))
                .andReturn();

        // Then
        String originToken = mvcResult.getResponse().getHeader(constants.HEADER_STRING);
        String token = originToken.replace(constants.TOKEN_PREFIX, "");

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(constants.JWT_SECRET))
                .build()
                .verify(token);

        assertThat(decodedJWT.getClaim("id").asLong()).isEqualTo(consumer.getMemberId());
        assertThat(decodedJWT.getClaim("role").asString()).isEqualTo(consumer.getAuthenticationType().authority());
        assertThat(decodedJWT.getClaim("username").asString()).isEqualTo(consumer.getEmail());
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
    @Test
    @DisplayName("잘못된 아이디로 로그인 시도 시, 오류 메시지 반환 확인")
    public void WhenAttemptToLoginWithInvalidId_ThenUnauthorizedException() throws Exception {
        // Given
        LoginRequest invalidLoginRequest = new LoginRequest("dsa@amer.com", "sss313213");

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
