package com.twogather.twogatherwebbackend.auth;

import com.twogather.twogatherwebbackend.dto.ErrorResponse;
import com.twogather.twogatherwebbackend.exception.CustomAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException){
        ErrorResponse errorResponse = new ErrorResponse(authException.getMessage());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setStatus(((CustomAuthenticationException)authException).getStatus().value());
        try {
            response.getWriter().write(errorResponse.toJson());
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
