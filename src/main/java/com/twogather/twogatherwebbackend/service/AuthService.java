package com.twogather.twogatherwebbackend.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;

import static com.twogather.twogatherwebbackend.auth.AuthMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final PrivateConstants constants;
    private final MemberRepository memberRepository;

    public String reissueAccessToken(String refreshToken) {
        try {
            CustomUser customUser = parseToken(refreshToken);
            saveSecurityContext(customUser);
            return constants.TOKEN_PREFIX + generateAccessToken(customUser);
        } catch (Exception e) {
            log.warn("Jwt processing failed: ", e);
            throw new BadCredentialsException(FAILURE_AUTH);
        }
    }

    private CustomUser parseToken(String token) {
        token = token.replace(constants.TOKEN_PREFIX, "");

        Long memberId = getMemberId(token);
        Optional<Member> member = memberRepository.findActiveMemberById(memberId);
        if(!member.isPresent()){
            throw new BadCredentialsException(FAILURE_AUTH);
        }
        return new CustomUser(member.get());
    }

    private Long getMemberId(String token){
        return JWT
                .require(Algorithm.HMAC512(constants.JWT_SECRET))
                .build()
                .verify(token)
                .getClaim("id").asLong();
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