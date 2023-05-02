package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.member.StoreOwnerResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreUpdateRequest;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Response> save(@RequestBody @Valid final StoreSaveRequest storeSaveRequest) {
        StoreResponse data = storeService.save(storeSaveRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }
    @PutMapping("/{storeId}")
    public ResponseEntity<Response> update(@PathVariable Long storeId, @RequestBody @Valid StoreUpdateRequest storeUpdateRequest) {
        StoreResponse data = storeService.update(storeId, storeUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> delete(@PathVariable Long storeId) {
        storeService.delete(storeId);

        return ResponseEntity.noContent().build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Response {
        private StoreResponse data;
    }
}
