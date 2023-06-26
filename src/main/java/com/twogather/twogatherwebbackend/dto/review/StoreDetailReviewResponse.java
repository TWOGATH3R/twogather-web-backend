package com.twogather.twogatherwebbackend.dto.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StoreDetailReviewResponse {
    private Long reviewId;
    private String content;
    private Double score;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
    private Long consumerId;
    private String consumerName;
    private Double consumerAvgScore;
}
