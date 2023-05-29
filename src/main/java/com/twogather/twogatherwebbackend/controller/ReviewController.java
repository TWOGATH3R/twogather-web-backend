package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.dto.review.MyReviewInfoResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewSaveRequest;
import com.twogather.twogatherwebbackend.dto.review.ReviewUpdateRequest;
import com.twogather.twogatherwebbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Response> upload(@PathVariable Long storeId, @RequestBody @Valid final ReviewSaveRequest request) {
        ReviewResponse data = reviewService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("@reviewService.isMyReeview(#reviewId)")
    public ResponseEntity<Response> delete(@PathVariable final Long reviewId, @PathVariable String storeId) {
        reviewService.delete(reviewId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("@reviewService.isMyReview(#reviewId)")
    public ResponseEntity<Response> update(@PathVariable final Long reviewId,
                                           @PathVariable final Long storeId,
                                           @RequestBody @Valid final ReviewUpdateRequest request){
        ReviewResponse data = reviewService.update(request);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }
    @GetMapping("/members/{memberId}")
    public ResponseEntity<Response> getMyReviewInfos(@PathVariable final Long memberId,
                                                     @PathVariable final Long storeId,
                                                     @RequestParam(defaultValue = "desc") final String orderBy,
                                                     @RequestParam(defaultValue = "createdAt") final String orderColumn,
                                                     @RequestParam(defaultValue = "1") final int page,
                                                     @RequestParam(defaultValue = "5") final int size) {
        Page<MyReviewInfoResponse> reviews = reviewService.getMyReviewInfos(memberId, orderBy, orderColumn, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(reviews));
    }

}
