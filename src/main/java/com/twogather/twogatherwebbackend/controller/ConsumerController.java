package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.ConsumerResponse;
import com.twogather.twogatherwebbackend.dto.member.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.service.ConsumerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@RestController
@RequestMapping("/api/consumers")
@RequiredArgsConstructor
public class ConsumerController {
    private final ConsumerService consumerService;

    @PostMapping()
    public ResponseEntity<Response> join(@RequestBody @Valid final ConsumerSaveRequest consumerSaveRequest) {
        ConsumerResponse data = consumerService.join(consumerSaveRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasAnyRole('CONSUMER')")
    public ResponseEntity<Response> getConsumerInfo(@PathVariable @Email String email) {
        ConsumerResponse data = consumerService.getMemberWithAuthorities(email);
        return ResponseEntity.ok(new Response(data));
    }

}