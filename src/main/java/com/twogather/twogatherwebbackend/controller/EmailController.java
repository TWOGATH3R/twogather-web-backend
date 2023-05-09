package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.email.EmailRequest;
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
    public ResponseEntity<String> sendMail(@RequestBody EmailRequest request) {
        String token = emailService.sendEmail(request.getEmail());
        return ResponseEntity.ok(token);
    }
}
