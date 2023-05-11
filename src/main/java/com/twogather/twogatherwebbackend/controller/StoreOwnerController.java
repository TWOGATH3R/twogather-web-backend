package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerResponse;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveUpdateRequest;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class StoreOwnerController {
    private final StoreOwnerService storeOwnerService;

    @PostMapping
    public ResponseEntity<Response> join(@RequestBody @Valid final StoreOwnerSaveUpdateRequest storeOwnerSaveUpdateRequest) {
        StoreOwnerResponse data = storeOwnerService.join(storeOwnerSaveUpdateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeOwnerService.isStoreOwner(#memberId)")
    public ResponseEntity<Response> getOwnerInfo(@PathVariable Long memberId){
        StoreOwnerResponse data = storeOwnerService.getMemberWithAuthorities(memberId);

        return ResponseEntity.ok(new Response(data));
    }


    @PutMapping("/{memberId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeOwnerService.isStoreOwner(#memberId)")
    public ResponseEntity<Response> updateOwnerInfo(@PathVariable Long memberId, @RequestBody @Valid final StoreOwnerSaveUpdateRequest storeOwnerSaveUpdateRequest){
        StoreOwnerResponse data = storeOwnerService.update(storeOwnerSaveUpdateRequest);

        return ResponseEntity.ok(new Response(data));
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeOwnerService.isStoreOwner(#memberId)")
    public ResponseEntity<Response> leave(@PathVariable Long memberId) {
        storeOwnerService.delete(memberId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }




}
