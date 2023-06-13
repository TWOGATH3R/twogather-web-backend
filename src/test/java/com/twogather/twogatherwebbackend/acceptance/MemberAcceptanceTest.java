package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.email.EmailRequest;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveRequest;
import com.twogather.twogatherwebbackend.dto.member.PasswordRequest;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.util.TestUtil.convert;
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

    private static final MemberSaveRequest UPDATE_REQUEST
            = new MemberSaveRequest(
                    UPDATE_EMAIL,
                    UPDATE_USERNAME,
                    UPDATE_PASSWORD,
                    UPDATE_NAME);

    private static final MemberSaveRequest UPDATE_REQUEST_EMPTY_VALUE
            = new MemberSaveRequest(
            "",
            null,
            null,
            "");

    @Test
    @DisplayName("owner 회원가입 성공")
    public void whenOwnerSignup_ThenSuccess(){
        //given, when
        Response result = doPost(OWNER_URL, null,null,OWNER_SAVE_REQUEST)
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
        Response result = doPost(CONSUMER_URL, null,null,CONSUMER_SAVE_REQUEST)
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
        doPost(CONSUMER_URL, null,null,CONSUMER_SAVE_REQUEST)
                .statusCode(HttpStatus.CREATED.value());

        // When, then
        doPost(CONSUMER_URL, null,null,CONSUMER_SAVE_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("유효하지 않은 정보로 회원가입 시도 시 실패 + 오류가 있는 필드에 대한 정보 제공")
    public void WhenOwnerSignupWithInvalidInfo_ThenBadRequest() throws Exception {
        // given
        MemberSaveRequest invalidRequest = new MemberSaveRequest("ascom","us1","pw","홍길@@동");

        // when, then
        doPost(OWNER_URL,null,null, invalidRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }
    @Test
    @DisplayName("consumer, storeOwner 동일한 아이디로 가입 시 실패")
    public void WhenSignUpWithSameUsernameByConsumerAndOwner_ThenThrowException() {
        // given
        MemberSaveRequest request = new MemberSaveRequest("asd@naver.com","user1","pw1asd2312","홍길동");

        //when, then
        doPost(OWNER_URL, null,null,request)
                .statusCode(HttpStatus.CREATED.value());

        doPost(CONSUMER_URL, null,null,request)
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }
    @Test
    @DisplayName("consumer, storeOwner 동일한 이메일로 가입 시 실패")
    public void WhenSignUpWithSameEmailByConsumerAndOwner_ThenThrowException() {
        // given
        MemberSaveRequest request1 = new MemberSaveRequest("asd@naver.com","user1","pw1asd2312","홍길동");
        MemberSaveRequest request2 = new MemberSaveRequest("asd@naver.com","user12","pw1asd2312","홍길동");

        //when, then
        doPost(OWNER_URL, null,null,request1)
                .statusCode(HttpStatus.CREATED.value());

        doPost(CONSUMER_URL,null,null, request2)
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

                ;
    }

    @Test
    @DisplayName("owner 비밀번호 변경 성공, 이전 회원정보로는 로그인 실패, 변경된 정보로 로그인 성공")
    public void whenOwnerChangePassword_ThenSuccess(){
        //given
        registerOwner();
        String UPDATE_URL = OWNER_URL + "/password";
        String newPassword = "newnew123";
        //when
        doPut(UPDATE_URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                new PasswordRequest(newPassword))
                .statusCode(HttpStatus.OK.value());

        //then
        doLogin(new LoginRequest(OWNER_USERNAME, newPassword));
        failLogin(new LoginRequest(OWNER_USERNAME, OWNER_PASSWORD));

    }

    @Test
    @DisplayName("consumer 비밀번호 변경 성공, 이전 회원정보로는 로그인 실패, 변경된 정보로 로그인 성공")
    public void whenConsumerChangePassword_ThenSuccess(){
        //given
        registerConsumer();
        String UPDATE_URL = CONSUMER_URL  + "/password";
        String newPassword = "newnew123";
        //when
        doPut(UPDATE_URL,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken(),
                new PasswordRequest(newPassword))
                .statusCode(HttpStatus.OK.value());

        //then
        doLogin(new LoginRequest(CONSUMER_USERNAME, newPassword));
        failLogin(new LoginRequest(CONSUMER_USERNAME, CONSUMER_PASSWORD));

    }
    @Test
    @DisplayName("consumer 개인정보 변경 성공, 변경된 정보로 로그인 성공")
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

    @Test
    @DisplayName("이미 존재하는 회원의 email로 email 검증 시도 시 throw exception")
    public void whenSendMailWithDuplicateEmail_ThenThrowException(){
        //given
        registerConsumer();
        //when,then
        String url = "/api/email";
        doPost(url, null,null,new EmailRequest(CONSUMER_EMAIL))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(DUPLICATE_EMAIL.getMessage()));
    }

    private <T> MemberResponse updateMember(String url, String refreshToken, String accessToken, T request){
        Response result = doPut(url, refreshToken, accessToken, request)
                .statusCode(HttpStatus.OK.value())
                .extract().as(Response.class);
        return convert(result, new TypeReference<MemberResponse>() {});
    }
}
