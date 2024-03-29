package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.common.Response;
import com.twogather.twogatherwebbackend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores/{storeId}/members/{memberId}/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    @PreAuthorize("@consumerService.isConsumer(#memberId)")
    public ResponseEntity<Response> addStoreLike(@PathVariable final Long memberId,
                                                @PathVariable final Long storeId) {
        likeService.addStoreLike(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    @PreAuthorize("@consumerService.isConsumer(#memberId)")
    public ResponseEntity<Response> deleteStoreLike(@PathVariable final Long memberId,
                                                    @PathVariable final Long storeId) {
        likeService.deleteStoreLike(storeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
