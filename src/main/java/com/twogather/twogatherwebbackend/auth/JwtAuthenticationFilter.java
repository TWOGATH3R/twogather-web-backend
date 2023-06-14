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
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository,
                                   JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                   PrivateConstants constants) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.constants = constants;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String token = request.getHeader(constants.ACCESS_TOKEN_HEADER);
            String accessToken = token.replace(constants.TOKEN_PREFIX, "");
            setAuthentication(accessToken);
        } catch (Exception e) {
            CustomUser customUser = useRefreshToken(request, response);
            if(customUser==null) return;
            saveSecurityContext(customUser);
        }
        chain.doFilter(request, response);
    }

    private void saveSecurityContext(CustomUser customUser){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUser, null, customUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    private CustomUser useRefreshToken(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = extractToken(request, response, constants.REFRESH_TOKEN_HEADER);
        CustomUser customUser = parseToken(request, response, refreshToken);
        if(customUser==null) return null;
        String accessToken = generateAccessToken(customUser);
        setAccessTokenHeader(response, accessToken);
        return customUser;
    }

    public CustomUser parseToken(HttpServletRequest request, HttpServletResponse response, String token) {
        Long memberId = null;
        try{
            memberId = getMemberId(token);
        }catch(TokenExpiredException e){
            handleAuthenticationFailure(request, response, new BadCredentialsException(EXPIRED_TOKEN));
            return null;
        }catch(Exception e){
            e.getStackTrace();
            return null;
        }
        Optional<Member> member = memberRepository.findActiveMemberById(memberId);
        if(!member.isPresent()){
            handleAuthenticationFailure(request, response, new BadCredentialsException(NO_SUCH_MEMBER));
            return null;
        }
        return new CustomUser(member.get());
    }

    private String extractToken(HttpServletRequest request, HttpServletResponse response, String header) {
        if (header == null) {
            handleAuthenticationFailure(request, response, new BadCredentialsException(INVALID_TOKEN));
            return null;
        }
        String token = request.getHeader(header);
        if(token==null || !token.startsWith(constants.TOKEN_PREFIX)){
            handleAuthenticationFailure(request, response, new BadCredentialsException(INVALID_TOKEN));
            return null;
        }

        return token.replace(constants.TOKEN_PREFIX, "");
    }


    private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken);
    }

    private void setAuthentication(String accessToken) {
        Long memberId = getMemberId(accessToken);
        Optional<Member> member = memberRepository.findActiveMemberById(memberId);
        CustomUser customUser = new CustomUser(member.get());
        saveSecurityContext(customUser);
    }
    private void handleAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)  {
        jwtAuthenticationEntryPoint.commence(request, response, e);
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
    private Long getMemberId(String token){
        return JWT
                .require(Algorithm.HMAC512(constants.JWT_SECRET))
                .build()
                .verify(token)
                .getClaim("id").asLong();
    }


}
