package com.twogather.twogatherwebbackend.acceptance;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.service.ConsumerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private ConsumerService consumerService;
    @Autowired
    private PrivateConstants constants;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String USERNAME ="user1";
    private final String PASSWORD=  passwordEncoder.encode("PASSWORD1234");


    @Test
    @DisplayName("refresh token만 유효할 경우 새로운 access토큰을 발급받아서 해당 access토큰이 로그인을 성공시키는지 확인")
    public void whenAccessTokenExpires_UseRefreshTokenToGenerateNewAccessToken() throws Exception {
        //given
        String refreshToken = generateRefreshToken();
        String expiredAccessToken = generateExpiredAccessToken();

        //when
        when(authenticationManager.authenticate(any(Authentication.class))).
                thenReturn(getMockAuthentication());
        when(memberRepository.findActiveMemberById(1l)).thenReturn(
               Optional.of(new Member(1l,USERNAME,
               "asd@naer.com",PASSWORD, "name1",
                       AuthenticationType.CONSUMER,true)));
        when(consumerService.isConsumer(1l)).thenReturn(true);

        //then
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/consumers/1")
                        .header(constants.ACCESS_TOKEN_HEADER, expiredAccessToken)
                        .header(constants.REFRESH_TOKEN_HEADER, refreshToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String newAccessToken = mvcResult.getResponse().getHeader(constants.ACCESS_TOKEN_HEADER);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/consumers/1")
                        .header(constants.ACCESS_TOKEN_HEADER, newAccessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
    private Authentication getMockAuthentication() {
        CustomUser customUser = new CustomUser(1L, USERNAME, "김머시기","PASSWORD1234",
                new ArrayList<>(){{
                    add(new SimpleGrantedAuthority("CUSTOMER"));
                }});
        return new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());
    }

    private String generateRefreshToken() {
        CustomUser customUser = new CustomUser(1L, USERNAME, "김머시기",PASSWORD,
                new ArrayList<>(){{
                    add(new SimpleGrantedAuthority("CUSTOMER"));
                }});
        return constants.TOKEN_PREFIX + JWT.create()
                .withSubject(customUser.getMemberId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + constants.REFRESH_TOKEN_EXPIRATION_TIME))
                .withClaim("id", customUser.getMemberId())
                .sign(Algorithm.HMAC512(constants.JWT_SECRET));
    }

    private String generateExpiredAccessToken() {
        CustomUser customUser = new CustomUser(1L, USERNAME, "김머시기","PASSWORD1234",
                new ArrayList<>(){{
                    add(new SimpleGrantedAuthority("CUSTOMER"));
                }});
        return constants.TOKEN_PREFIX + JWT.create()
                .withSubject(customUser.getMemberId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000)) // Set the expiration time to a past time
                .withClaim("id", customUser.getMemberId())
                .withClaim("role", customUser.getRole())
                .sign(Algorithm.HMAC512(constants.JWT_SECRET));
    }
}
