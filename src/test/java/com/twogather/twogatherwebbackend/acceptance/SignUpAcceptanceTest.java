package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
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
    @DisplayName("owner 회원가입 성공")
    public void whenOwnerSignup_ThenSuccess() throws Exception {
        // When
        MemberSaveUpdateRequest request = new MemberSaveUpdateRequest("asd@naver.com","user1","pwsadasd12312","홍길동");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        // Then
        StoreOwner owner = ownerRepository.findByUsername(request.getUsername()).get();
        assertThat(owner.getName()).isEqualTo(request.getName());
        assertThat(owner.getEmail()).isEqualTo(request.getEmail());
        assertThat(owner.getPassword()).isNotBlank();
    }

    @Test
    @DisplayName("consumer 회원가입")
    public void WhenConsumerSignup_ThenSuccess() throws Exception {
        // When
        MemberSaveUpdateRequest request = new MemberSaveUpdateRequest("asd@naver.com","user1","pwsadasd12312","홍길동");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        // Then
        Consumer consumer = consumerRepository.findByUsername(request.getUsername()).get();
        assertThat(consumer.getEmail()).isEqualTo(request.getEmail());
        assertThat(consumer.getName()).isEqualTo(request.getName());

        em.flush();
        em.clear();
        Consumer savedConsumer = consumerRepository.findByUsername(request.getUsername()).get();
        Assertions.assertEquals(savedConsumer.getUsername(),request.getUsername());


    }

    @Test
    @DisplayName("동일한 loginId 회원가입 시도시 에러 응답이 잘 반환돼야 함")
    public void WhenSignupWithDuplicateEmail_ThenBadRequest() throws Exception {
        Consumer consumer1 = CONSUMER;
        consumerRepository.save(consumer1);

        MemberSaveUpdateRequest request = new MemberSaveUpdateRequest("asd@naver.com",consumer1.getUsername(),"pasdw12312","홍길동");
        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        // Then
        consumerRepository.findByUsername(request.getUsername()).get();

        HttpServletResponse response = mvcResult.getResponse();
        response.setCharacterEncoding("UTF-8");

        String jsonString = ((MockHttpServletResponse) response).getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        String message = jsonNode.get("message").asText();
        assertThat(message).isEqualTo(DUPLICATE_USERNAME.getMessage());
    }

    @Test
    @DisplayName("유효하지 않은 정보로 회원가입 시도 시 실패 + 오류가 있는 필드에 대한 정보 제공")
    public void WhenOwnerSignupWithInvalidInfo_ThenBadRequest() throws Exception {
        // When
        MemberSaveUpdateRequest invalidRequest = new MemberSaveUpdateRequest("ascom","us1","pw","홍길@@동");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
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
            Set<String> expectedFields = new HashSet<>(Arrays.asList("password", "name", "username", "email"));
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
    @DisplayName("consumer, storeOwner 동일한 아이디로 가입 시 실패")
    public void WhenSignUpWithSameUsernameByConsumerAndOwner_ThenThrowException() throws Exception {
        // When
        MemberSaveUpdateRequest request = new MemberSaveUpdateRequest("asd@naver.com","user1","pw1asd2312","홍길동");

        ownerRepository.save(new StoreOwner(request.getUsername(), "fd@naer.com", "adsasd123", "김김김",AuthenticationType.STORE_OWNER,true));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.message").value("로그인 아이디가 중복됩니다"))
                .andReturn();

    }
}
