package com.twogather.twogatherwebbackend.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.dto.RefreshTokenValue;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.RefreshTokenRepository;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.twogather.twogatherwebbackend.auth.AuthMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final PrivateConstants constants;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public String reissueAccessToken(String refreshToken) {
        try {
            refreshToken = refreshToken.replace(constants.TOKEN_PREFIX,"");
            Long memberId = refreshTokenRepository.findId(refreshToken).get();
            CustomUser customUser = new CustomUser(memberRepository.findById(Long.valueOf(memberId)).get());
            saveSecurityContext(customUser);
            return constants.TOKEN_PREFIX + generateAccessToken(customUser);
        } catch (Exception e) {
            log.warn("Jwt processing failed: ", e);
            throw new BadCredentialsException(FAILURE_AUTH);
        }
    }

    private void saveSecurityContext(CustomUser customUser){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUser, null, customUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    private String generateAccessToken(CustomUser customUser) {
        return JWT.create()
                .withSubject(customUser.getMemberId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + constants.ACCESS_TOKEN_EXPIRATION_TIME))
                .withExpiresAt(new Date(System.currentTimeMillis() + constants.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("id", customUser.getMemberId())
                .withClaim("role", customUser.getRole())
                .sign(Algorithm.HMAC512(constants.JWT_SECRET));
    }
}