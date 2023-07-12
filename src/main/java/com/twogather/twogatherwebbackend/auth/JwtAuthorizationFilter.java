package com.twogather.twogatherwebbackend.auth;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.twogather.twogatherwebbackend.dto.RefreshTokenValue;
import com.twogather.twogatherwebbackend.repository.RefreshTokenRepository;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.auth.AuthMessage.*;

@Slf4j
public class JwtAuthorizationFilter extends UsernamePasswordAuthenticationFilter{

    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final PrivateConstants constants;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                  PrivateConstants constants,
                                  RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.constants = constants;
        this.refreshTokenRepository = refreshTokenRepository;
        this.setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("JwtAuthorizationFilter : 진입");

            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            log.info("JwtAuthorizationFilter: {}", loginRequest);

            validateLoginRequest(loginRequest);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword());

            System.out.println("JwtAuthorizationFilter : 토큰생성완료");
            return authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Authentication failed: invalid username or password");
            commenceAuthenticationFailure(request, response, new BadCredentialsException(FAILURE_LOGIN));
            return null;
        }
    }
    private void commenceAuthenticationFailure(HttpServletRequest request,
                                               HttpServletResponse response,
                                               AuthenticationException e) {
        jwtAuthenticationEntryPoint.commence(request, response, e);
    }

    @Override
    @Transactional
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {
        CustomUser customUser = (CustomUser) authResult.getPrincipal();

        String accessToken = generateToken(customUser, constants.ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = generateToken(customUser, constants.REFRESH_TOKEN_EXPIRATION_TIME);

        refreshTokenRepository.save(refreshToken, customUser.getMemberId());
        writeTokensToResponse(customUser, response, accessToken, refreshToken);
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
        String loginResponseJson = objectMapper.writeValueAsString(new Response<>(new LoginResponse(customUser.getMemberId(), customUser.getName())));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.addHeader(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken);
        response.addHeader(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX +  refreshToken);
        response.getWriter().write(loginResponseJson);
    }
    private void validateLoginRequest(LoginRequest loginRequest) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        if (!violations.isEmpty()) {
            throw new BadCredentialsException(FAILURE_LOGIN);
       }
    }
}
