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
import com.twogather.twogatherwebbackend.exception.CustomAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.*;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private PrivateConstants constants;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                   PrivateConstants constants) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.setFilterProcessesUrl("/api/login"); // 로그인 URL 변경
        this.constants = constants;

    }

    // Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
    // 인증 요청시에 실행되는 함수 => /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        log.info("JwtAuthenticationFilter : 진입");

        // request에 있는 username과 password를 파싱해서 자바 Object로 받기
        ObjectMapper om = new ObjectMapper();
        LoginRequest loginRequest = createLoginRequest(om, request);

        log.info("JwtAuthenticationFilter: {}", loginRequest);

        // 유저네임패스워드 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword());

        System.out.println("JwtAuthenticationFilter : 토큰생성완료");

        // authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
        // loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
        // UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
        // UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
        // Authentication 객체를 만들어서 필터체인으로 리턴해준다.

        try {
            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);
            User user = (User) authentication.getPrincipal();
            log.info("user info: {}", user.getUsername());
            return authentication;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            log.info("Authentication failed: invalid username or password");
            jwtAuthenticationEntryPoint.commence(request, response, new CustomAuthenticationException(INVALID_ID_AND_PASSWORD));
            return null;
        }
    }

    private LoginRequest createLoginRequest(ObjectMapper om, HttpServletRequest request){
        try {
            LoginRequest loginRequest = om.readValue(request.getInputStream(), LoginRequest.class);
            return loginRequest;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("{}: , now: {}", LOGIN_FAILURE.getMessage(), LocalDateTime.now());
            throw new CustomAuthenticationException(LOGIN_FAILURE);
        }
    }
    // 액세스 토큰 생성
    private String generateAccessToken(CustomUser customUser) {
        return JWT.create()
                .withSubject(customUser.getMemberId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + constants.ACCESS_TOKEN_EXPIRATION_TIME))
                .withClaim("id", customUser.getMemberId())
                .withClaim("role", customUser.getRole())
                .sign(Algorithm.HMAC512(constants.JWT_SECRET));
    }

    // 리프레시 토큰과 함께 응답 생성
    private void writeTokensToResponse(CustomUser customUser, HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String loginResponseJson = objectMapper.writeValueAsString(new Response<>(new LoginResponse(customUser.getMemberId())));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.addHeader(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken);
        response.addHeader(constants.REFRESH_TOKEN_HEADER,  refreshToken);
        response.getWriter().write(loginResponseJson);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {

        CustomUser customUser = (CustomUser) authResult.getPrincipal();

        String accessToken = generateAccessToken(customUser);
        String refreshToken = generateRefreshToken(customUser);

        writeTokensToResponse(customUser, response, accessToken, refreshToken);
    }
    // 리프레시 토큰 생성
    private String generateRefreshToken(CustomUser customUser) {
        return JWT.create()
                .withSubject(customUser.getMemberId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + constants.REFRESH_TOKEN_EXPIRATION_TIME))
                .withClaim("id", customUser.getMemberId())
                .sign(Algorithm.HMAC512(constants.JWT_SECRET));
    }

}