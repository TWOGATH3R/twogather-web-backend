package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.comment.CommentResponse;
import com.twogather.twogatherwebbackend.dto.comment.CommentSaveUpdateRequest;
import com.twogather.twogatherwebbackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/stores/{storeId}/reviews/{reviewId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    
    @PostMapping
    @PreAuthorize("@storeService.isMyStore(#storeId)")  // 가게 사장님만 대댓글 작성 가능
    public ResponseEntity<Response> upload(@PathVariable Long storeId,
                                           @PathVariable Long reviewId,
                                           @RequestBody @Valid final CommentSaveUpdateRequest request) {
        CommentResponse data = commentService.save(reviewId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(data));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or (@commentService.isMyComment(#commentId) and @storeService.isMyStore(#storeId))")
    public ResponseEntity<Response> update(@PathVariable Long storeId,
                                           @PathVariable Long reviewId,
                                           @PathVariable Long commentId,
                                           @RequestBody @Valid final CommentSaveUpdateRequest request) {
        CommentResponse data = commentService.update(commentId, request);

        return ResponseEntity.status(HttpStatus.OK).body(new Response(data));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or (@commentService.isMyComment(#commentId) and @storeService.isMyStore(#storeId))")
    public ResponseEntity delete(@PathVariable Long storeId,
                                           @PathVariable Long reviewId,
                                           @PathVariable Long commentId) {
        commentService.delete(commentId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
