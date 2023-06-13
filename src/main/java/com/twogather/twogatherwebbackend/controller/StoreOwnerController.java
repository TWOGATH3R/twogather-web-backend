package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.*;
import com.twogather.twogatherwebbackend.service.MemberService;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class StoreOwnerController {
    private final StoreOwnerService storeOwnerService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Response> join(@RequestBody @Valid final MemberSaveRequest storeOwnerSaveUpdateRequest) {
        MemberResponse data = storeOwnerService.join(storeOwnerSaveUpdateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<Response> verifyPassword(@RequestBody PasswordRequest request) {
        boolean passwordMatches = memberService.verifyPassword(request.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(new Response(new VerifyPasswordResponse(passwordMatches)));
    }

    @PutMapping("/password")
    public ResponseEntity<Response> changePassword(@RequestBody PasswordRequest request) {
        memberService.changePassword(request.getPassword());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeOwnerService.isStoreOwner(#memberId)")
    public ResponseEntity<Response> getOwnerInfo(@PathVariable Long memberId){
        MemberResponse data = storeOwnerService.getMemberWithAuthorities(memberId);

        return ResponseEntity.ok(new Response(data));
    }

    @PutMapping("/{memberId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeOwnerService.isStoreOwner(#memberId)")
    public ResponseEntity<Response> updateOwnerInfo(@PathVariable Long memberId, @RequestBody @Valid final MemberUpdateRequest storeOwnerSaveUpdateRequest){
        MemberResponse data = memberService.update(storeOwnerSaveUpdateRequest);

        return ResponseEntity.ok(new Response(data));
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeOwnerService.isStoreOwner(#memberId)")
    public ResponseEntity<Response> leave(@PathVariable Long memberId) {
        storeOwnerService.delete(memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
