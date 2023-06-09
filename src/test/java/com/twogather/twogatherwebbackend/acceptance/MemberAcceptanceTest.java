package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.TestUtil.convert;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.DUPLICATE_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class MemberAcceptanceTest extends AcceptanceTest{

    @Autowired
    private MemberRepository memberRepository;

    private static final String UPDATE_EMAIL ="NEWasd@naver.com";
    private static final String UPDATE_USERNAME ="NEWuser1";
    private static final String UPDATE_PASSWORD ="NEWasdAsad123";
    private static final String UPDATE_NAME ="NEW홍길동";

    private static final MemberSaveUpdateRequest UPDATE_REQUEST
            = new MemberSaveUpdateRequest(
                    UPDATE_EMAIL,
                    UPDATE_USERNAME,
                    UPDATE_PASSWORD,
                    UPDATE_NAME);

    @Test
    @DisplayName("owner 회원가입 성공")
    public void whenOwnerSignup_ThenSuccess(){
        //given, when
        Response result = doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST)
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(Response.class);

        MemberResponse response = convert(result, new TypeReference<MemberResponse>() {});

        //then
        Assertions.assertTrue(memberRepository.findById(response.getMemberId()).isPresent());
    }

    @Test
    @DisplayName("consumer 회원가입")
    public void WhenConsumerSignup_ThenSuccess() {
        //given, when
        Response result = doPost(CONSUMER_URL, CONSUMER_SAVE_UPDATE_REQUEST)
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(Response.class);

        MemberResponse response =  convert(result, new TypeReference<MemberResponse>() {});

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
    @Test
    @DisplayName("consumer, storeOwner 동일한 이메일로 가입 시 실패")
    public void WhenSignUpWithSameEmailByConsumerAndOwner_ThenThrowException() {
        // given
        MemberSaveUpdateRequest request1 = new MemberSaveUpdateRequest("asd@naver.com","user1","pw1asd2312","홍길동");
        MemberSaveUpdateRequest request2 = new MemberSaveUpdateRequest("asd@naver.com","user12","pw1asd2312","홍길동");

        //when, then
        doPost(OWNER_URL, request1)
                .statusCode(HttpStatus.CREATED.value());

        doPost(CONSUMER_URL, request2)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(DUPLICATE_EMAIL.getMessage()));

    }
    @Test
    @DisplayName("owner 개인정보 변경 성공")
    public void whenOwnerChangeInfo_ThenSuccess(){
        //given
        registerOwner();
        String UPDATE_URL = OWNER_URL + "/" + loginMemberId;

        //when
        MemberResponse response = updateMember(UPDATE_URL, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), UPDATE_REQUEST);

        //then
        Member member = memberRepository.findById(response.getMemberId()).get();

        Assertions.assertEquals(member.getEmail(), UPDATE_REQUEST.getEmail());
        Assertions.assertEquals(member.getName(), UPDATE_REQUEST.getName());

        doLogin(new LoginRequest(UPDATE_USERNAME, UPDATE_PASSWORD));

        failLogin(new LoginRequest(OWNER_SAVE_UPDATE_REQUEST.getUsername(), OWNER_SAVE_UPDATE_REQUEST.getPassword()))
                .statusCode(HttpStatus.UNAUTHORIZED.value());
                ;
    }
    @Test
    @DisplayName("consumer 개인정보 변경 성공")
    public void whenConsumerChangeInfo_ThenSuccess(){
        //given
        registerConsumer();
        String UPDATE_URL = CONSUMER_URL + "/" + loginMemberId;

        //when
        MemberResponse response = updateMember(UPDATE_URL, consumerToken.getRefreshToken(), consumerToken.getAccessToken(), UPDATE_REQUEST);

        //then
        Member member = memberRepository.findById(response.getMemberId()).get();

        Assertions.assertEquals(member.getEmail(), UPDATE_REQUEST.getEmail());
        Assertions.assertEquals(member.getName(), UPDATE_REQUEST.getName());

        doLogin(new LoginRequest(UPDATE_USERNAME, UPDATE_PASSWORD));

        failLogin(new LoginRequest(CONSUMER_SAVE_UPDATE_REQUEST.getUsername(), CONSUMER_SAVE_UPDATE_REQUEST.getPassword()))
                .statusCode(HttpStatus.UNAUTHORIZED.value());
        ;
    }

    @Test
    @DisplayName("owner 탈퇴 후 로그인 실패")
    public void whenAfterOwnerLeaved_ThenThrowException(){
        //given
        registerOwner();
        //when
        leaveOwner();
        //then
        failLogin(OWNER_LOGIN_REQUEST)
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }


    @Test
    @DisplayName("consumer 탈퇴 후 로그인 실패")
    public void whenAfterConsumerLeaved_ThenThrowException(){
        //given
        registerConsumer();
        //when
        leaveConsumer();
        //then
        failLogin(CONSUMER_LOGIN_REQUEST)
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    private <T> MemberResponse updateMember(String url, String refreshToken, String accessToken, T request){
        Response result = doPut(url, refreshToken, accessToken, request)
                .statusCode(HttpStatus.OK.value())
                .extract().as(Response.class);
        return convert(result, new TypeReference<MemberResponse>() {});
    }
}
