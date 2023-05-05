package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerResponse;
import com.twogather.twogatherwebbackend.dto.store.MyStoreResponse;
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
import java.util.List;

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

    @GetMapping("/{storeId}")
    public ResponseEntity<Response> getStoreInfo(@PathVariable Long storeId) {
        StoreResponse data = storeService.getStore(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping("/my")
    public ResponseEntity<Response> getMyStoreInfo(
            @RequestParam(value = "owner-id") Long storeOwnerId,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset
    ) {
        List<MyStoreResponse> data = storeService.getStoresByOwner(storeOwnerId, limit, offset);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping
    public ResponseEntity<Response> getStoreInfos(  @RequestParam(value = "category", required = false) String categoryName,
                                                    @RequestParam(value = "search", required = false) String keyword,
                                                    @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                    @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
                                                    @RequestParam(value = "order", defaultValue = "asc") String order) {
        List<StoreResponse> data = storeService.getStores(categoryName, keyword, limit, offset, orderBy, order);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> delete(@PathVariable Long storeId) {
        storeService.delete(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
