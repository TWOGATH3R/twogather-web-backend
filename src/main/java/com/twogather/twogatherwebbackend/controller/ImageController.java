package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.image.ImageIdList;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    @PostMapping("/images")
    public ResponseEntity<Response> upload(List<MultipartFile> fileList) {
        List<ImageResponse> data = imageService.upload(fileList);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }
    @GetMapping("/stores/{storeId}/images")
    public ResponseEntity<Response> getStoreImageInfos(@PathVariable Long storeId) {
        List<ImageResponse> data = imageService.getStoreImageInfos(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
    @DeleteMapping("/images")
    public ResponseEntity<Response> delete(ImageIdList idList) {
        imageService.delete(idList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
