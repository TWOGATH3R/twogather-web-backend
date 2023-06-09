package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final StoreService storeService;
    @PatchMapping("/stores/{storeId}")
    public ResponseEntity<Response> approveStore(@PathVariable Long storeId){
        storeService.approveStore(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
