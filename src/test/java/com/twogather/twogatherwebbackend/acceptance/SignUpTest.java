package com.twogather.twogatherwebbackend.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.auth.JwtProperties;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.ErrorResponse;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.INVALID_ID_AND_PASSWORD;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.DUPLICATE_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class SignUpTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreOwnerRepository storeOwnerRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init(){
        //LocalDate 직렬화/역직렬화를 위해 필요한 모듈
        objectMapper.registerModule(new JavaTimeModule());;
    }

    @Test
    @Transactional
    @DisplayName("owner 회원가입")
    public void WhenOwnerSignup_ThenSuccess() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OWNER_SAVE_REQUEST2)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        // Then
        StoreOwner owner = storeOwnerRepository.findByEmail(OWNER_SAVE_REQUEST2.getEmail()).get();
        assertThat(owner.getEmail()).isEqualTo(OWNER_SAVE_REQUEST2.getEmail());
        assertThat(owner.getBusinessName()).isEqualTo(OWNER_SAVE_REQUEST2.getBusinessName());
        assertThat(owner.getBusinessNumber()).isEqualTo(OWNER_SAVE_REQUEST2.getBusinessNumber());
        assertThat(owner.getBusinessStartDate()).isEqualTo(OWNER_SAVE_REQUEST2.getBusinessStartDate());
        assertThat(owner.getName()).isEqualTo(OWNER_SAVE_REQUEST2.getName());

    }

    @Test
    @Transactional
    @DisplayName("consumer 회원가입")
    public void WhenConsumerSignup_ThenSuccess() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CONSUMER_SAVE_UPDATE_REQUEST2)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        // Then
        Consumer consumer = consumerRepository.findByEmail(CONSUMER_SAVE_UPDATE_REQUEST2.getEmail()).get();
        assertThat(consumer.getEmail()).isEqualTo(CONSUMER_SAVE_UPDATE_REQUEST2.getEmail());
        assertThat(consumer.getName()).isEqualTo(CONSUMER_SAVE_UPDATE_REQUEST2.getName());

    }

    @Test
    @DisplayName("동일한 이메일로 회원가입 시도시 에러 응답이 잘 반환돼야 함")
    public void WhenSignupWithDuplicateEmail_ThenBadRequest() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OWNER_SAVE_REQUEST)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        // Then
        storeOwnerRepository.findByEmail(OWNER_SAVE_REQUEST.getEmail()).get();

        HttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding("UTF-8");

        String jsonString = ((MockHttpServletResponse) response).getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        String message = jsonNode.get("message").asText();
        assertThat(message).isEqualTo(DUPLICATE_EMAIL.getMessage());
    }

}
