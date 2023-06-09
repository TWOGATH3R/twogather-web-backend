package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.auth.JwtAccessDeniedHandler;
import com.twogather.twogatherwebbackend.auth.JwtAuthenticationEntryPoint;
import com.twogather.twogatherwebbackend.auth.JwtAuthorizationFilter;
import com.twogather.twogatherwebbackend.auth.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.web.servlet.MockMvc;


// controller unit test for api docs
public class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected PasswordEncoder passwordEncoder;

    @MockBean
    protected UserDetailsService userDetailsService;

    @MockBean
    protected AuthenticationEntryPoint authenticationEntryPoint;

    @MockBean
    protected AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockBean
    protected AuthenticationFailureHandler authenticationFailureHandler;

    @MockBean
    protected AccessDeniedHandler accessDeniedHandler;

    @MockBean
    protected JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    protected JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    protected JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;


    protected ObjectMapper objectMapper = new ObjectMapper();
}
