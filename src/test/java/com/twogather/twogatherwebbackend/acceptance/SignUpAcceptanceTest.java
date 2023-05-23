package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.exception.InvalidArgumentException.InvalidArgumentErrorCode.INVALID_ARGUMENT;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.DUPLICATE_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class SignUpAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StoreOwnerRepository ownerRepository;
    @Autowired
    private EntityManager em;

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
    @DisplayName("owner 회원가입 - 사업자 등록번호 검증 실패")
    public void WhenOwnerSignupWithInvalidBusinessNumber_ThenErrorResponse() throws Exception {
        // When
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OWNER_SAVE_REQUEST2)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        // Then
        assertThat(result.getResolvedException()).isInstanceOf(MemberException.class);
        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(responseContent).contains(MemberException.MemberErrorCode.BIZ_REG_NUMBER_VALIDATION.getMessage());
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
        Consumer consumer = consumerRepository.findByUsername(CONSUMER_SAVE_UPDATE_REQUEST2.getUsername()).get();
        assertThat(consumer.getEmail()).isEqualTo(CONSUMER_SAVE_UPDATE_REQUEST2.getEmail());
        assertThat(consumer.getName()).isEqualTo(CONSUMER_SAVE_UPDATE_REQUEST2.getName());

        em.flush();
        em.clear();
        Consumer consumer1 = consumerRepository.findByUsername(CONSUMER_SAVE_UPDATE_REQUEST2.getUsername()).get();
        Assertions.assertEquals(consumer1.getUsername(),CONSUMER_SAVE_UPDATE_REQUEST2.getUsername());


    }

    @Test
    @DisplayName("동일한 loginId 회원가입 시도시 에러 응답이 잘 반환돼야 함")
    public void WhenSignupWithDuplicateEmail_ThenBadRequest() throws Exception {
        initSetting();

        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CONSUMER_SAVE_UPDATE_REQUEST)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        // Then
        consumerRepository.findByUsername(CONSUMER_SAVE_UPDATE_REQUEST.getUsername()).get();

        HttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding("UTF-8");

        String jsonString = ((MockHttpServletResponse) response).getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        String message = jsonNode.get("message").asText();
        assertThat(message).isEqualTo(DUPLICATE_USERNAME.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("유효하지 않은 정보로 회원가입 시도 시 실패 + 오류가 있는 필드에 대한 정보 제공")
    public void WhenOwnerSignupWithInvalidInfo_ThenBadRequest() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_OWNER_SAVE_REQUEST)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        // Then
        HttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding("UTF-8");

        String jsonString = ((MockHttpServletResponse) response).getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        JsonNode errorsNode = jsonNode.get("errors");
        String message = jsonNode.get("message").asText();
        assertThat(message).isEqualTo(INVALID_ARGUMENT.getMessage());

        if (errorsNode != null && errorsNode.isObject()) {
            Set<String> expectedFields = new HashSet<>(Arrays.asList("password", "businessStartDate","name", "businessNumber", "email"));
            Set<String> actualFields = new HashSet<>();

            Iterator<Map.Entry<String, JsonNode>> fields = errorsNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                actualFields.add(entry.getKey());
            }

            if (expectedFields.equals(actualFields)) {
                // 모든 필드가 존재함
                System.out.println("모든 필드가 존재합니다.");
            } else {
                // 일부 필드가 누락됨
                System.out.println("일부 필드가 누락되었습니다.");
                Set<String> missingFields = new HashSet<>(expectedFields);
                missingFields.removeAll(actualFields);
                System.out.println("누락된 필드: " + missingFields);
            }
            assertThat(expectedFields).isEqualTo(actualFields);
        }

    }

    private void initSetting(){
        Consumer consumer1 = CONSUMER;
        consumerRepository.save(consumer1);
    }

    @Test
    @Transactional
    @DisplayName("consumer, storeOwner 동일한 아이디로 가입 시 실패")
    public void WhenSignUpWithSameUsernameByConsumerAndOwner_ThenThrowException() throws Exception {
        // When
        ownerRepository.save(new StoreOwner(CONSUMER_SAVE_UPDATE_REQUEST2.getUsername(), "fd@naer.com", "adsasd123", "김김김","123123","김김김", LocalDate.now(), AuthenticationType.STORE_OWNER,true));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CONSUMER_SAVE_UPDATE_REQUEST2)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.message").value("로그인 아이디가 중복됩니다"))
                .andReturn();

    }
}
