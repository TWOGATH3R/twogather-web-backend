package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.image.ImageIdList;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.service.ImageService;
import com.twogather.twogatherwebbackend.service.StoreOwnerService;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final StoreService storeService;
    @PostMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> upload(@PathVariable Long storeId, List<MultipartFile> fileList) {
        List<ImageResponse> data = imageService.upload(fileList);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }
    @GetMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> getStoreImageInfos(@PathVariable Long storeId) {
        List<ImageResponse> data = imageService.getStoreImageInfos(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
    @DeleteMapping
    @PreAuthorize("hasRole('STORE_OWNER') and @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> delete(@PathVariable Long storeId, ImageIdList idList) {
        imageService.delete(idList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
