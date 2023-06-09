package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.PagedResponse;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.review.*;
import com.twogather.twogatherwebbackend.service.ReviewService;
import com.twogather.twogatherwebbackend.service.StoreService;
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
    private final StoreService storeService;

    @PostMapping("/stores/{storeId}/reviews")
    @PreAuthorize("not @storeService.isMyStore(#storeId)")
    public ResponseEntity<Response> upload(@PathVariable Long storeId, @RequestBody @Valid final ReviewSaveRequest request) {
        ReviewResponse data = reviewService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @DeleteMapping("/stores/{storeId}/reviews/{reviewId}")
    @PreAuthorize("@reviewService.isMyReeview(#reviewId)")
    public ResponseEntity<Response> delete(@PathVariable final Long reviewId, @PathVariable String storeId) {
        reviewService.delete(reviewId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/stores/{storeId}/reviews/{reviewId}")
    @PreAuthorize("@reviewService.isMyReview(#reviewId)")
    public ResponseEntity<Response> update(@PathVariable final Long reviewId, @PathVariable final Long storeId,
                                           @RequestBody @Valid final ReviewUpdateRequest request){
        ReviewResponse data = reviewService.update(request);

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
