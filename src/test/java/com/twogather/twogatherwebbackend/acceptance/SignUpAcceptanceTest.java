package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
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


public class SignUpAcceptanceTest extends AcceptanceTest{

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("owner 회원가입 성공")
    public void whenOwnerSignup_ThenSuccess(){
        //given, when
        MemberResponse response =
                new ObjectMapper().convertValue(
                doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST)
                        .statusCode(HttpStatus.CREATED.value())
                        .extract().as(Response.class).getData()
                ,new TypeReference<>() {});
        //then
        Assertions.assertTrue(memberRepository.findById(response.getMemberId()).isPresent());
    }

    @Test
    @DisplayName("consumer 회원가입")
    public void WhenConsumerSignup_ThenSuccess() {
        // given, when
        MemberResponse response =
                new ObjectMapper().convertValue(
                        doPost(CONSUMER_URL, CONSUMER_SAVE_UPDATE_REQUEST)
                                .statusCode(HttpStatus.CREATED.value())
                                .extract().as(Response.class).getData()
                        ,new TypeReference<>() {});

        //then
        Assertions.assertTrue(memberRepository.findById(response.getMemberId()).isPresent());

    }

    @Test
    @DisplayName("동일한 loginId 회원가입 시도시 에러 응답이 잘 반환돼야 함")
    public void WhenSignupWithDuplicateEmail_ThenBadRequest() {
        //given
        doPost(CONSUMER_URL, CONSUMER_SAVE_UPDATE_REQUEST)
                .statusCode(HttpStatus.CREATED.value());
        // When, then
        doPost(CONSUMER_URL, CONSUMER_SAVE_UPDATE_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("유효하지 않은 정보로 회원가입 시도 시 실패 + 오류가 있는 필드에 대한 정보 제공")
    public void WhenOwnerSignupWithInvalidInfo_ThenBadRequest() throws Exception {
        // given
        MemberSaveUpdateRequest invalidRequest = new MemberSaveUpdateRequest("ascom","us1","pw","홍길@@동");
        // when, then
        doPost(OWNER_URL, invalidRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }
    @Test
    @DisplayName("consumer, storeOwner 동일한 아이디로 가입 시 실패")
    public void WhenSignUpWithSameUsernameByConsumerAndOwner_ThenThrowException() {
        // given
        MemberSaveUpdateRequest request = new MemberSaveUpdateRequest("asd@naver.com","user1","pw1asd2312","홍길동");

        //when, then
        doPost(OWNER_URL, request)
                .statusCode(HttpStatus.CREATED.value());
        doPost(CONSUMER_URL, request)
                .statusCode(HttpStatus.BAD_REQUEST.value());


    }
}
