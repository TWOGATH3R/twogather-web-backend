package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.common.PagedResponse;
import com.twogather.twogatherwebbackend.dto.common.Response;
import com.twogather.twogatherwebbackend.dto.email.EmailRequest;
import com.twogather.twogatherwebbackend.dto.member.FindUsernameRequest;
import com.twogather.twogatherwebbackend.dto.member.PasswordRequest;
import com.twogather.twogatherwebbackend.dto.member.VerifyPasswordResponse;
import com.twogather.twogatherwebbackend.dto.store.MyLikeStoreResponse;
import com.twogather.twogatherwebbackend.service.MemberService;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final StoreService storeService;
    @PostMapping("/checks-email")
    public ResponseEntity<Response> checkEmailIsExist(@Valid @RequestBody EmailRequest request) {
        Boolean isExist = memberService.isExist(request);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(isExist));
    }
    @PostMapping("/my-id")
    public ResponseEntity<Response> findMyUsername(@Valid @RequestBody FindUsernameRequest request) {
        String username = memberService.findMyUsername(request);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(username));
    }

    @GetMapping("/{memberId}/likes")
    @PreAuthorize("@memberService.isMyId(#memberId)")
    public ResponseEntity<Response> findLikeStore(@PathVariable Long memberId,
                                                  Pageable pageable) {
        Page<MyLikeStoreResponse> response = storeService.findMyLikeStore(memberId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(response));
    }

    @PostMapping("/{memberId}/verify-password")
    @PreAuthorize("@memberService.isMyId(#memberId)")
    public ResponseEntity<Response> verifyPassword(@PathVariable Long memberId,
                                                   @Valid @RequestBody PasswordRequest request) {
        boolean passwordMatches = memberService.verifyPassword(request.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(new Response(new VerifyPasswordResponse(passwordMatches)));
    }

    @PutMapping("/{memberId}/password")
    @PreAuthorize("@memberService.isMyId(#memberId)")
    public ResponseEntity<Response> changePassword(@PathVariable Long memberId,
                                                   @Valid @RequestBody PasswordRequest request) {
        memberService.changePassword(request.getPassword());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
