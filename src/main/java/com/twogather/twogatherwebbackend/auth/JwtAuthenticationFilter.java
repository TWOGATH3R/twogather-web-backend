package com.twogather.twogatherwebbackend.auth;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import static com.twogather.twogatherwebbackend.auth.AuthMessage.*;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;
    private final PrivateConstants constants;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository,
                                   PrivateConstants constants) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.constants = constants;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String token = request.getHeader(constants.ACCESS_TOKEN_HEADER);
            String accessToken = token.replace(constants.TOKEN_PREFIX, "");
            setAuthentication(accessToken);
        } catch (Exception e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), FAILURE_AUTH);
            return;
        }
        chain.doFilter(request, response);
    }

    private void saveSecurityContext(CustomUser customUser){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUser, null, customUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setAuthentication(String accessToken) {
        Long memberId = getMemberId(accessToken);
        Optional<Member> member = memberRepository.findActiveMemberById(memberId);
        CustomUser customUser = new CustomUser(member.get());
        saveSecurityContext(customUser);
    }

    private Long getMemberId(String token){
        return JWT
                .require(Algorithm.HMAC512(constants.JWT_SECRET))
                .build()
                .verify(token)
                .getClaim("id").asLong();
    }


}