package com.twogather.twogatherwebbackend.dto.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyReviewInfoResponse extends ReviewResponse{
    private String url;
    private String storeName;
    private String storeAddress;

    public MyReviewInfoResponse(Long reviewId, String content, Double score, LocalDate createdDate,
                                String url, String storeName, String storeAddress, String consumerName) {
        super(reviewId, content, score, createdDate, consumerName);
        this.url = url;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
    }
}
