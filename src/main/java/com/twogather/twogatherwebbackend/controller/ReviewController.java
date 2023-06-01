package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.review.*;
import com.twogather.twogatherwebbackend.service.ReviewService;
import com.twogather.twogatherwebbackend.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/stores/{storeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> upload(@PathVariable Long storeId, @RequestBody @Valid final ReviewSaveRequest request) {
        /* TODO
            인증되지 않은 사용자 또는 가게 주인은 리뷰를 등록할 수 없도록 처리
            인증되지 않은 사용자: @PreAuthorize("isAuthenticated()") 사용
            가게 주인: 임시로 StoreService에 구현된 isMyStore() 사용. 리팩토링 필요
        */

        if(storeService.isMyStore(storeId)) {
            // 가게 주인은 자신의 가게에 리뷰를 등록할 수 없음
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // 가게 주인이 아닌 로그인한 사용자는 리뷰 등록 가능
        ReviewResponse data = reviewService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated() and @reviewService.isMyReview(#reviewId)")
    public ResponseEntity<Response> delete(@PathVariable final Long reviewId, @PathVariable String storeId) {
        reviewService.delete(reviewId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated() and @reviewService.isMyReview(#reviewId)")
    public ResponseEntity<Response> update(@PathVariable final Long reviewId, @PathVariable final Long storeId,
                                           @RequestBody @Valid final ReviewUpdateRequest request){
        ReviewResponse data = reviewService.update(request);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @GetMapping("/members/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Response> getMyReviewInfos(@PathVariable final Long memberId,
                                                     @PathVariable final Long storeId,
                                                     @RequestParam(defaultValue = "desc") final String orderBy,
                                                     @RequestParam(defaultValue = "createdDate") final String orderColumn,
                                                     @RequestParam(defaultValue = "0") final int page,
                                                     @RequestParam(defaultValue = "5") final int size) {
        Page<MyReviewInfoResponse> reviews = reviewService.getMyReviewInfos(memberId, orderBy, orderColumn, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(reviews));
    }

    @GetMapping
    public ResponseEntity<Response> getReviewsByStoreId(@PathVariable final Long storeId,
                                                     @RequestParam(defaultValue = "desc") final String orderBy,
                                                     @RequestParam(defaultValue = "createdDate") final String orderColumn,
                                                     @RequestParam(defaultValue = "0") final int page,
                                                     @RequestParam(defaultValue = "10") final int size) {
        Page<StoreDetailReviewResponse> reviews = reviewService.getReviewsByStoreId(storeId, orderBy, orderColumn, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(new Response(reviews));
    }

}
