package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.store.StoreSaveRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreUpdateRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreUpdateResponse;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid final StoreSaveRequest storeSaveRequest) {
        StoreSaveResponse storeSaveResponse = storeService.save(storeSaveRequest);

        return ResponseEntity.created(URI.create("/api/stores/" + storeSaveResponse.getId())).build();
    }
    @PutMapping("/{storeId}")
    public ResponseEntity<Void> update(@PathVariable Long storeId, @RequestBody @Valid StoreUpdateRequest storeUpdateRequest) {
        StoreUpdateResponse storeUpdateResponse = storeService.update(storeId, storeUpdateRequest);

        return ResponseEntity.created(URI.create("/api/stores/" + storeUpdateResponse.getId())).build();
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> delete(@PathVariable Long storeId) {
        storeService.delete(storeId);

        return ResponseEntity.noContent().build();
    }
}
