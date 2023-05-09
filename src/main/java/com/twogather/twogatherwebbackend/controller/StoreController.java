package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.PagedResponse;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerResponse;
import com.twogather.twogatherwebbackend.dto.store.*;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/keyword")
    public ResponseEntity<Response> getKeywordList() {
        List<String> data = storeService.getKeyword();

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping("/my")
    public ResponseEntity<Response> getMyStoreInfo(
            @RequestParam(value = "owner-id") Long storeOwnerId,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset
    ) {
        Page<MyStoreResponse> data = storeService.getStoresByOwner(storeOwnerId, limit, offset);

        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(data));
    }

    @GetMapping("/top-preview")
    public ResponseEntity<Response> getStoreTopPreviewInfos() {
        TopStoreInfoPreviewResponse data = storeService.getStoresTop10Preview();

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
    @GetMapping("/top/{type}")
    public ResponseEntity<Response> getStoreTopInfos(@PathVariable String type) {
        List<TopStoreInfoResponse> data = storeService.getStoresTop10(type);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> getStoreInfos(  @RequestParam(value = "category", required = false) String categoryName,
                                                    @RequestParam(value = "search", required = false) String keyword,
                                                    @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                    @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
                                                    @RequestParam(value = "order", defaultValue = "asc") String order,
                                                    @RequestParam(value = "location", required = false) String location) {
        List<StoresResponse> data = storeService.getStores(categoryName, keyword, limit, offset, orderBy, order, location);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> delete(@PathVariable Long storeId) {
        storeService.delete(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
