package com.twogather.twogatherwebbackend.dto.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewResponse {
    private Long consumerId;
    private Long reviewId;
    private String content;
    private Double score;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
}
