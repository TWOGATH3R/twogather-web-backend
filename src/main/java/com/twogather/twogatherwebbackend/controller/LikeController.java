package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.menu.MenuResponse;
import com.twogather.twogatherwebbackend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    @PostMapping
    @PreAuthorize("not @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> addStoreLike(@PathVariable final Long storeId) {
        likeService.addStoreLike(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Response> deleteStoreLike(@PathVariable final Long storeId) {
        likeService.deleteStoreLike(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
