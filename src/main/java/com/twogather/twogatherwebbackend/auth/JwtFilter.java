package com.twogather.twogatherwebbackend.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtFilter extends GenericFilterBean {
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static TokenProvider tokenProvider;
    private static final List<String> loginUrl = Arrays.asList("/api/consumers", "/api/owners");
    public static final List<String> permitUrl = Arrays.asList("/error", "/api/login/token", "/api/logout", "/api/mail");

    public JwtFilter(TokenProvider tokenProvider, JwtAuthenticationEntryPoint authenticationEntryPoint){
        this.tokenProvider = tokenProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    // JWT토큰 인증정보를 Security context에 저장하기 위한 로직이 들어갈 것임
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();
        try {
            if (!shouldFilter(requestURI, httpServletRequest)) {
                log.info("permiall에 해당하는 url로 요청이 들어왔습니다. url: {}", requestURI);
            } else if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 '{}' 인증정보를 저장했습니다, url: {}", authentication.getName(), requestURI);
            } else {
                log.debug("유효한 JWT 토큰이 없습니다, url: {}", requestURI);
                throw new BadCredentialsException("INVALID JWT TOKEN");
            }
            chain.doFilter(servletRequest, servletResponse);
        } catch (BadCredentialsException e) {
            authenticationEntryPoint.commence((HttpServletRequest)servletRequest, (HttpServletResponse) servletResponse, e);
        }
    }
    private boolean shouldFilter(String url, HttpServletRequest request){
        for (String urlOne :loginUrl) {
            if(urlOne.equals(url) && request.getMethod().equals(HttpMethod.POST.name())) return false;
        }
        for (String urlOne :permitUrl) {
            if(urlOne.equals(url)) return false;
        }
        return true;
    }
    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
        return null;
    }
}
