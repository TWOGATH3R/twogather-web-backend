package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.auth.JwtFilter;
import com.twogather.twogatherwebbackend.auth.TokenProvider;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.LoginInfo;
import com.twogather.twogatherwebbackend.dto.member.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.service.ConsumerService;
import com.twogather.twogatherwebbackend.service.CustomUserDetailsService;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/login/token")
    public ResponseEntity<LoginResponse> authorize(@Valid @RequestBody LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);
        Long memberId = ((CustomUser)authentication.getPrincipal()).getMemberId();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(new LoginResponse(new LoginInfo(memberId)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        new SecurityContextLogoutHandler().logout(request, null, null);
        return ResponseEntity.ok().build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    private class LoginResponse{
        LoginInfo data;
    }
}
