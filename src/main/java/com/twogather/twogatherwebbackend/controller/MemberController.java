package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.FindUsernameRequest;
import com.twogather.twogatherwebbackend.dto.member.PasswordRequest;
import com.twogather.twogatherwebbackend.dto.member.VerifyPasswordResponse;
import com.twogather.twogatherwebbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;


    @PostMapping("/my-id")
    public ResponseEntity<Response> findMyUsername(@RequestBody FindUsernameRequest request) {
        String username = memberService.findMyUsername(request);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(username));
    }

    @PostMapping("/{memberId}/verify-password")
    @PreAuthorize("@memberService.isMyId(#memberId)")
    public ResponseEntity<Response> verifyPassword(@PathVariable Long memberId,
                                                   @RequestBody PasswordRequest request) {
        boolean passwordMatches = memberService.verifyPassword(request.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(new Response(new VerifyPasswordResponse(passwordMatches)));
    }

    @PutMapping("/{memberId}/password")
    @PreAuthorize("@memberService.isMyId(#memberId)")
    public ResponseEntity<Response> changePassword(@PathVariable Long memberId,
                                                   @RequestBody PasswordRequest request) {
        memberService.changePassword(request.getPassword());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
