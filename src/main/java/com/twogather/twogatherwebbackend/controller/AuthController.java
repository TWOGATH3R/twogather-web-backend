package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/access-token")
    public ResponseEntity reissueAccessTokenByRefreshToken(
            @RequestHeader(value = "RefreshToken") String refreshToken
    ) {
        String accessToken =
                authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok().header("Authorization", accessToken).build();
    }
}
