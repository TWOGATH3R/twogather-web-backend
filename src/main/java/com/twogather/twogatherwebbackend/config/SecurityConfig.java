package com.twogather.twogatherwebbackend.config;


import com.twogather.twogatherwebbackend.auth.*;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsConfig corsConfig;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final PrivateConstants constants;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().and()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/api/stores/**/comments").authenticated()
                .antMatchers("/api/my/stores").authenticated()
                .antMatchers(HttpMethod.GET, "/api/stores/**").permitAll()
                .antMatchers(HttpMethod.POST,"/api/owners").authenticated()
                .antMatchers("/api/stores/**/business-hours").authenticated()
                .antMatchers(HttpMethod.GET, "/api/stores/**/business-hours").permitAll()
                .antMatchers("/api/**/categories").authenticated()
                .antMatchers("/api/consumers/**").authenticated()
                .antMatchers(HttpMethod.POST,"/api/consumers").permitAll()
                .antMatchers("/api/owners/**").authenticated()
                .antMatchers(HttpMethod.POST,"/api/owners").permitAll()
                .antMatchers("/api/email").permitAll()
                .antMatchers("/api/stores/**/images").authenticated()
                .antMatchers(HttpMethod.GET,"/api/keywords").permitAll()
                .antMatchers("/api/keywords/stores/**").authenticated()
                .antMatchers("/api/stores/**/likes").authenticated()
                .antMatchers("/api/stores/**/menus").authenticated()
                .antMatchers(HttpMethod.GET,"/api/stores/**/menus").permitAll()
                .antMatchers("/api/stores/**/reviews").authenticated()
                .and()
                .apply(new MyCustomDsl()) // 커스텀 필터 등록
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler).and()// access denied 시 JwtAccessDeniedHandler 실행
                .build();
    }

    @Bean
    public WebSecurityCustomizer ignoreStaticResources() {
        return (web) -> web.ignoring()
                .antMatchers(HttpMethod.GET, "/api/stores/**")
                .antMatchers(HttpMethod.POST,"/api/owners")
                .antMatchers(HttpMethod.POST,"/api/consumers")
                .antMatchers("/api/email")
                .antMatchers("/api/members/checks-email")
                .antMatchers("/api/**/my-id")
                .antMatchers("/api/categories")
                .antMatchers(HttpMethod.GET,"/api/keywords")
                .antMatchers(HttpMethod.GET,"/api/stores/**/menus")
                .antMatchers(HttpMethod.GET, "/api/stores/**/business-hours");
    }
    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager, memberRepository, jwtAuthenticationEntryPoint, constants))
                    .addFilterAfter(new JwtAuthorizationFilter(authenticationManager, jwtAuthenticationEntryPoint, constants), JwtAuthorizationFilter.class);
        }

    }

}
