package com.twogather.twogatherwebbackend.dto.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StoreDetailReviewResponse extends ReviewResponse{
    private Long consumerId;
    private Double consumerAvgScore;

    public StoreDetailReviewResponse(Long consumerId, Long reviewId, String content, Double score,
                              LocalDate createdDate, String consumerName, Double consumerAvgScore) {
        super(reviewId, content, score, createdDate, consumerName);
        this.consumerId = consumerId;
        this.consumerAvgScore = consumerAvgScore;
    }
}
