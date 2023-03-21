package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.LoginRequest;
import com.twogather.twogatherwebbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login/token")
    public ResponseEntity<Void> login(@RequestBody @Valid final LoginRequest loginRequest) {
        authService.login(loginRequest);
        return ResponseEntity.ok().contentType(MediaType.valueOf("application/json; charset=UTF-8")).build();
    }
}
