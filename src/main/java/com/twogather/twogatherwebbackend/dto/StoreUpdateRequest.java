package com.twogather.twogatherwebbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreUpdateRequest {
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String name;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String address;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;
}
