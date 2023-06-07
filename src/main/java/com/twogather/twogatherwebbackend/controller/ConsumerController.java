package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.member.VerifyPasswordResponse;
import com.twogather.twogatherwebbackend.service.ConsumerService;
import com.twogather.twogatherwebbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/consumers")
@RequiredArgsConstructor
public class ConsumerController {
    private final MemberService memberService;
    private final ConsumerService consumerService;

    @PostMapping("/verify-password")
    public ResponseEntity<Response> verifyPassword(@RequestBody String password) {
        boolean passwordMatches = memberService.verifyPassword(password);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(new VerifyPasswordResponse(passwordMatches)));
    }

    @PostMapping
    public ResponseEntity<Response> join(@RequestBody @Valid final MemberSaveUpdateRequest consumerSaveRequest) {
        MemberResponse data = consumerService.join(consumerSaveRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @PutMapping("/{memberId}")
    @PreAuthorize("hasRole('CONSUMER') and @consumerService.isConsumer(#memberId)")
    public ResponseEntity<Response> updateConsumerInfo(@PathVariable final Long memberId, @RequestBody @Valid final MemberSaveUpdateRequest consumerSaveUpdateRequest){
        MemberResponse data = memberService.update(consumerSaveUpdateRequest);

        return ResponseEntity.ok(new Response(data));
    }
    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('CONSUMER') and @consumerService.isConsumer(#memberId)")
    public ResponseEntity<Response> leave(@PathVariable Long memberId) {
        consumerService.delete(memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('CONSUMER') and @consumerService.isConsumer(#memberId)")
    public ResponseEntity<Response> getConsumerInfo(@PathVariable final Long memberId) {
        MemberResponse data = consumerService.getConsumerInfo(memberId);
        return ResponseEntity.ok(new Response(data));
    }

}