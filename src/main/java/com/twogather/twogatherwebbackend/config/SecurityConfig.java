package com.twogather.twogatherwebbackend.config;

import com.twogather.twogatherwebbackend.auth.JwtAccessDeniedHandler;
import com.twogather.twogatherwebbackend.auth.JwtAuthenticationEntryPoint;
import com.twogather.twogatherwebbackend.auth.JwtFilter;
import com.twogather.twogatherwebbackend.auth.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.twogather.twogatherwebbackend.domain.AuthenticationType.*;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)// @PreAuthorize 을 메서드 단위로 추가하기 위해서 적용
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    //AUTOWIRED없이 어떻게 주입받는거지
    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ){
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 토큰방식을 사용하기 때문에 disable
                .csrf().disable()
                // 우리가 설정한 handler로 설정
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                // h2 console을 위한 설정
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()
                // 세션사용안하니까 세션x 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeHttpRequests()
                .antMatchers("/api/login/token").permitAll()
                .antMatchers("/api/logout").permitAll()
                .antMatchers(HttpMethod.POST, "/api/owners").permitAll()
                .antMatchers(HttpMethod.POST, "/api/consumers").permitAll()
                .antMatchers("/api/owners/**").hasRole(OWNER.authority())
                .antMatchers("/api/consumers/**").hasRole(CONSUMER.authority())
                // jwtFilter를 적용했던 jwtSecurityConfig 클래스도 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider, jwtAuthenticationEntryPoint));

        return http.build();
    }
}