package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.auth.TokenProvider;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.exception.AuthException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CustomUserDetailsService userDetailsService;
    private AuthService authService;
    static final String token = "token";

    @BeforeEach
    void setUp() {
        authService = new AuthService(memberRepository, passwordEncoder, tokenProvider);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 exception(validatePassword 테스트를 위함)")
    public void whenLogin_ShouldInvalidPassword() {
        //given
        LoginRequest loginRequest = OWNER_INVALID_LOGIN_REQUEST;
        when(memberRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(STORE_OWNER));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

        //when, then
        Assertions.assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(MemberException.class)
                .describedAs(MemberException.MemberErrorCode.PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("TestConstants 에 저장된 정보로 로그인시에 passwordEncoder 잘 동작하여 로그인 성공")
    @Test
    public void whenUseConstantsLogin_ShouldSuccess(){
        //given
        LoginRequest loginRequest = OWNER_LOGIN_REQUEST;
        when(memberRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(STORE_OWNER));
        when(tokenProvider.createToken(any(), any())).thenReturn(token);
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);

        //when
        AuthService.TokenAndId tokenAndId = authService.login(loginRequest);
        //then
        Assertions.assertThat(tokenAndId.getToken()).isNotBlank();
    }
}
