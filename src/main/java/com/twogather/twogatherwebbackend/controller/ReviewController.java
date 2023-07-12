package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.common.PagedResponse;
import com.twogather.twogatherwebbackend.dto.common.Response;
import com.twogather.twogatherwebbackend.dto.review.*;
import com.twogather.twogatherwebbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/stores/{storeId}/reviews")
    @PreAuthorize("not @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> upload(@PathVariable Long storeId, @RequestBody @Valid final ReviewSaveUpdateRequest request) {
        ReviewResponse data = reviewService.save(storeId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @DeleteMapping("/stores/{storeId}/reviews/{reviewId}")
    @PreAuthorize("hasRole('ADMIN') or (@reviewService.isMyReview(#reviewId) and not @storeService.isMyStore(#storeId))")
    public ResponseEntity<Response> delete(@PathVariable final Long reviewId, @PathVariable String storeId) {
        reviewService.delete(reviewId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/stores/{storeId}/reviews/{reviewId}")
    @PreAuthorize("hasRole('ADMIN') or (@reviewService.isMyReview(#reviewId) and not @storeService.isMyStore(#storeId))")
    public ResponseEntity<Response> update(@PathVariable final Long reviewId, @PathVariable final Long storeId,
                                           @RequestBody @Valid final ReviewSaveUpdateRequest request){
        ReviewResponse data = reviewService.update(reviewId, request);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping("/reviews/members/{memberId}")
    public ResponseEntity<Response> getMyReviewInfos(@PathVariable final Long memberId, Pageable pageable) {
        Page<MyReviewInfoResponse> reviews = reviewService.getMyReviewInfos(memberId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(reviews));
    }

    @GetMapping("/stores/{storeId}/reviews")
    public ResponseEntity<Response> getReviewsByStoreId(@PathVariable final Long storeId, Pageable pageable) {
        Page<StoreDetailReviewResponse> reviews = reviewService.getReviewsByStoreId(storeId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new PagedResponse(reviews));
    }

}
