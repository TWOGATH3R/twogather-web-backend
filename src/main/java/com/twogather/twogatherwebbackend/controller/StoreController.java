package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.PagedResponse;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.StoreSearchType;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveListRequest;
import com.twogather.twogatherwebbackend.dto.store.*;

import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @PostMapping(value = "/categories/{categoryId}")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<Response> save(
                                        @PathVariable Long categoryId,
                                        @RequestPart @Valid final BusinessHourSaveUpdateListRequest businessHourRequest,
                                        @RequestPart @Valid final StoreSaveUpdateRequest storeRequest,
                                        @RequestPart @Valid final MenuSaveListRequest menuRequest,
                                        @RequestPart List<MultipartFile> storeImageList,
                                        @RequestPart final List<String> keywordList
                                         ) {
        StoreSaveUpdateResponse data =
                storeService.save(categoryId,
                        businessHourRequest,
                        storeRequest,
                        menuRequest,
                        storeImageList,
                        keywordList);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @PutMapping("/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> update(@PathVariable Long storeId, @RequestBody @Valid StoreSaveUpdateRequest storeUpdateRequest) {
        StoreSaveUpdateResponse data = storeService.update(storeId, storeUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @PatchMapping("/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> reapply(@PathVariable Long storeId) {
        storeService.reapply(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/{storeId}")
    public ResponseEntity<Response> getStoreInfo(@PathVariable Long storeId) {
        StoreSaveUpdateResponse data = storeService.getStore(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }


    @GetMapping("/top/{type}/{count}")
    public ResponseEntity<Response> getStoreTopInfos(@PathVariable StoreSearchType type,
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
        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(data));
    }

    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Void> delete(@PathVariable Long storeId) {
        storeService.delete(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
