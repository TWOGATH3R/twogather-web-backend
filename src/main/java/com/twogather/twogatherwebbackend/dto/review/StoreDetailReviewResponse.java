package com.twogather.twogatherwebbackend.dto.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twogather.twogatherwebbackend.dto.comment.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class StoreDetailReviewResponse {
    private Long reviewId;
    private String content;
    private Double score;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    private Long consumerId;
    private String consumerName;
    private Double consumerAvgScore;
    private CommentResponse comment;

    public StoreDetailReviewResponse(Long reviewId, String content, Double score, LocalDateTime createdDate,
                                     Long consumerId, String consumerName, Double consumerAvgScore,
                                     CommentResponse commentResponse) {
        this.reviewId = reviewId;
        this.content = content;
        this.score = score;
        this.createdDate = createdDate;
        this.consumerId = consumerId;
        this.consumerName = consumerName;
        this.consumerAvgScore = Math.round(consumerAvgScore * 10) / 10.0;
        this.comment = commentResponse;
    }
}
