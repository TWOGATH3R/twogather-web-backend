package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.common.PagedResponse;
import com.twogather.twogatherwebbackend.dto.common.Response;
import com.twogather.twogatherwebbackend.dto.store.StoreSearchType;
import com.twogather.twogatherwebbackend.dto.store.*;

import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/stores/test")
    public String test(){
        return "test";
    }

    @PostMapping(value = "/stores")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<Response> save(@RequestBody @Valid final StoreSaveUpdateRequest storeRequest) {
        StoreSaveUpdateResponse data = storeService.save(storeRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @PutMapping("/stores/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> update(@PathVariable Long storeId, @RequestBody @Valid StoreSaveUpdateRequest storeUpdateRequest) {
        StoreSaveUpdateResponse data = storeService.update(storeId, storeUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @PatchMapping("/stores/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> reapply(@PathVariable Long storeId) {
        storeService.reapply(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<Response> getStoreInfo(@PathVariable Long storeId) {
        StoreDefaultResponse data = storeService.getStore(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }


    @GetMapping("/stores/top/{type}/{count}")
    public ResponseEntity<Response> getStoreTopInfos(@PathVariable StoreSearchType type,
                                                     @PathVariable int count) {
        List<TopStoreResponse> data = storeService.getStoresTopN(type, count);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping("/my/stores")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeOwnerService.isStoreOwner(#ownerId)")
    public ResponseEntity<Response> getMyStoreInfo(
            @RequestParam(value = "ownerId") Long ownerId,
            Pageable pageable) {
        Page<MyStoreResponse> data = storeService.getStoresByOwner(ownerId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(data));
    }

    @GetMapping("/my/stores/{storeId}/detail")
    @PreAuthorize("hasRole('ADMIN') or @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> getMyStoreDetailInfo(@PathVariable Long storeId) {
        StoreSaveUpdateResponse data = storeService.getStoreDetail(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @PostMapping("/stores/detail")
    public ResponseEntity<Response> setStoreDetail() {
        storeService.setAllStoreDetail();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/stores/search")
    public ResponseEntity<Response> getStoreInfos(Pageable pageable,
                                                  @RequestParam(value = "category", required = false) String categoryName,
                                                  @RequestParam(value = "search", required = false) String keyword,
                                                  @RequestParam(value = "storeName", required = false) String storeName,
                                                  @RequestParam(value = "location", required = false) String location,
                                                  @RequestParam(value = "useSearchBtn")  Boolean useSearchBtn) {

        Page<StoreResponseWithKeyword> data = storeService.getStores(pageable, categoryName, keyword, location, storeName, useSearchBtn);
        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(data));
    }

    @DeleteMapping("/stores/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Void> delete(@PathVariable Long storeId) {
        storeService.delete(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
