package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.PagedResponse;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.StoreType;
import com.twogather.twogatherwebbackend.dto.store.*;

import com.twogather.twogatherwebbackend.service.StoreKeywordService;
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
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final StoreKeywordService storeKeywordService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @PostMapping
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<Response> save(@RequestBody @Valid final StoreSaveUpdateRequest storeSaveUpdateRequest) {
        StoreSaveUpdateResponse data = storeService.save(storeSaveUpdateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @PutMapping("/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> update(@PathVariable Long storeId, @RequestBody @Valid StoreSaveUpdateRequest storeUpdateRequest) {
        StoreSaveUpdateResponse data = storeService.update(storeId, storeUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<Response> getStoreInfo(@PathVariable Long storeId) {
        StoreSaveUpdateResponse data = storeService.getStore(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping("/keyword")
    public ResponseEntity<Response> getKeywordList() {
        List<String> data = storeService.getKeyword();

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }


    @GetMapping("/top/{type}/{count}")
    public ResponseEntity<Response> getStoreTopInfos(@PathVariable StoreType type,
                                                     @PathVariable int count) {
        List<TopStoreResponse> data = storeService.getStoresTopN(type, count);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping("/{storeId}/my")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> getMyStoreInfo(
            @PathVariable Long storeId,
            @RequestParam(value = "owner-id") Long storeOwnerId,
            Pageable pageable
    ) {
        Page<MyStoreResponse> data = storeService.getStoresByOwner(storeOwnerId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(data));
    }


    @GetMapping("/search")
    public ResponseEntity<Response> getStoreInfos(Pageable pageable,
                                                  @RequestParam(value = "category", required = false) String categoryName,
                                                  @RequestParam(value = "search", required = false) String keyword,
                                                  @RequestParam(value = "location", required = false) String location) {

        Page<StoreResponseWithKeyword> data = storeService.getStores(pageable, categoryName, keyword, location);
        return ResponseEntity.status(HttpStatus.OK).body(new Response(data.getContent()));
    }

    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Void> delete(@PathVariable Long storeId) {
        storeService.delete(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
