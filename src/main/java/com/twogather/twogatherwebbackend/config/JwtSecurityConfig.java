package com.twogather.twogatherwebbackend.config;

import com.twogather.twogatherwebbackend.auth.JwtAuthenticationEntryPoint;
import com.twogather.twogatherwebbackend.auth.JwtFilter;
import com.twogather.twogatherwebbackend.auth.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    public JwtSecurityConfig(TokenProvider tokenProvider, JwtAuthenticationEntryPoint authenticationEntryPoint){
        this.tokenProvider = tokenProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    @Override
    public void configure(HttpSecurity http){
        JwtFilter customFilter = new JwtFilter(tokenProvider,authenticationEntryPoint);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
