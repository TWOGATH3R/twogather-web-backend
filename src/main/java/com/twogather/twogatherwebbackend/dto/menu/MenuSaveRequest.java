package com.twogather.twogatherwebbackend.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuSaveRequest {
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String name;
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    @Min(value = 0, message = "가격은 양수여야 합니다.")
    private Integer price;
}
