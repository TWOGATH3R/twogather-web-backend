package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.member.ConsumerInfoResponse;
import com.twogather.twogatherwebbackend.dto.member.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.net.URI;

@RestController
@RequestMapping("/api/consumers")
@RequiredArgsConstructor
public class ConsumerController {
    private final ConsumerService consumerService;

    @PostMapping()
    public ResponseEntity<Void> join(@RequestBody @Valid final ConsumerSaveRequest consumerSaveRequest) {
        consumerService.join(consumerSaveRequest);

        return ResponseEntity.created(URI.create("/api/consumers/")).build();
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasAnyRole('CONSUMER')")
    public ResponseEntity<ConsumerInfoResponse> getConsumerInfo(@PathVariable @Email String email) {
        return ResponseEntity.ok(consumerService.getMemberWithAuthorities(email));
    }
}
