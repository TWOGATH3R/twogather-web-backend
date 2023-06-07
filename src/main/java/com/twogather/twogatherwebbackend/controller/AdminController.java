package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.dto.PagedResponse;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.store.MyStoreResponse;
import com.twogather.twogatherwebbackend.dto.store.RejectReason;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @PatchMapping("/stores/approve/{storeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> approveStore(@PathVariable Long storeId){
        storeService.approveStore(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PatchMapping("/stores/reject/{storeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> rejectStore(@PathVariable Long storeId,
                                                @RequestBody RejectReason rejectReason){
        storeService.rejectStore(storeId, rejectReason);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/stores/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse> getAllStore(@PathVariable StoreStatus type, Pageable pageable){
        Page<MyStoreResponse> response =  storeService.getStores(type, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(response));
    }
}
