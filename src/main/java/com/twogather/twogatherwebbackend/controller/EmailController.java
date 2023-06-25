package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.email.EmailRequest;
import com.twogather.twogatherwebbackend.dto.email.VerificationCodeResponse;
import com.twogather.twogatherwebbackend.dto.member.FindPasswordRequest;
import com.twogather.twogatherwebbackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Response> sendJoinMail(@RequestBody EmailRequest request) {
        VerificationCodeResponse verificationCodeResponse = emailService.sendCodeEmail(request.getEmail());
        return ResponseEntity.ok(new Response(verificationCodeResponse));
    }

    @PostMapping("/password")
    public ResponseEntity<Response> sendPasswordChangeEmail(@RequestBody FindPasswordRequest request) {
        emailService.requestEmailChangePassword(request);
        return ResponseEntity.ok().build();
    }
}
