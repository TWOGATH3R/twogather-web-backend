package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.member.StoreOwnerResponse;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @PostMapping()
    public ResponseEntity<Response> join(@RequestBody @Valid final StoreOwnerSaveRequest storeOwnerSaveRequest) {
        StoreOwnerResponse data = storeOwnerService.join(storeOwnerSaveRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Response> getOwnerInfo(@PathVariable @Email String email){
        StoreOwnerResponse data = storeOwnerService.getMemberWithAuthorities(email);

        return ResponseEntity.ok(new Response(data));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    private static class Response {
        private StoreOwnerResponse data;
    }


}
