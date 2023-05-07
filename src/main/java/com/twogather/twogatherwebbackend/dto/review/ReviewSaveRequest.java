package com.twogather.twogatherwebbackend.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewSaveRequest {
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Long consumerId;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Long storeId;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private String content;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    @DecimalMin(value = "0.0", message = "리뷰 점수는 0.0점 이상이어야 합니다.")
    @DecimalMax(value = "5.0", message = "리뷰 점수는 5.0점 이하여야 합니다.")
    @Digits(integer = 1, fraction = 1, message = "리뷰 점수는 소수점 첫째 자리까지만 입력 가능합니다.")
    private Double score;
}
