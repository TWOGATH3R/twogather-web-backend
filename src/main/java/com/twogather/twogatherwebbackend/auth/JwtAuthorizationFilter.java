package com.twogather.twogatherwebbackend.auth;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.twogather.twogatherwebbackend.dto.LoginResponse;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.twogather.twogatherwebbackend.auth.AuthMessage.NO_SUCH_MEMBER;

@Slf4j
public class JwtAuthorizationFilter extends UsernamePasswordAuthenticationFilter{

    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final PrivateConstants constants;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                  PrivateConstants constants) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.constants = constants;
        this.setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("JwtAuthorizationFilter : 진입");

        LoginRequest loginRequest = parseLoginRequest(request);
        log.info("JwtAuthorizationFilter: {}", loginRequest);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword());

        System.out.println("JwtAuthorizationFilter : 토큰생성완료");

        try {
            Authentication authentication =
                    authenticationManager.authenticate(authToken);
            User user = (User) authentication.getPrincipal();
            log.info("user info: {}", user.getUsername());
            return authentication;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            log.info("Authentication failed: invalid username or password");
            commenceAuthenticationFailure(request, response, new BadCredentialsException(NO_SUCH_MEMBER));
            return null;
        }
    }

    private LoginRequest parseLoginRequest(HttpServletRequest request) {
        try {
            return new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void commenceAuthenticationFailure(HttpServletRequest request,
                                               HttpServletResponse response,
                                               AuthenticationException e) {
        jwtAuthenticationEntryPoint.commence(request, response, e);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {
        CustomUser customUser = (CustomUser) authResult.getPrincipal();

        String accessToken = generateAccessToken(customUser);
        String refreshToken = generateRefreshToken(customUser);

        writeTokensToResponse(customUser, response, accessToken, refreshToken);
    }

    private String generateAccessToken(CustomUser customUser) {
        return generateToken(customUser, constants.ACCESS_TOKEN_EXPIRATION_TIME);
    }

    private String generateRefreshToken(CustomUser customUser) {
        return generateToken(customUser, constants.REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String generateToken(CustomUser customUser, long expirationTime) {
        return JWT.create()
                .withSubject(customUser.getMemberId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withClaim("id", customUser.getMemberId())
                .withClaim("role", customUser.getRole())
                .sign(Algorithm.HMAC512(constants.JWT_SECRET));
    }

    private void writeTokensToResponse(CustomUser customUser, HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String loginResponseJson = objectMapper.writeValueAsString(new Response<>(new LoginResponse(customUser.getMemberId())));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.addHeader(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken);
        response.addHeader(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX +  refreshToken);
        response.getWriter().write(loginResponseJson);
    }
}
