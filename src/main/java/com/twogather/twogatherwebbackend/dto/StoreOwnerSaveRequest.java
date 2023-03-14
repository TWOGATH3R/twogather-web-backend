package com.twogather.twogatherwebbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoreOwnerSaveRequest extends UserSave {
    @Size(min = 10, max = 10, message = "숫자는 10자리여야 합니다.")
    @Digits(integer = 10,fraction = 0, message = "숫자로 이루어져야 합니다")
    @NotBlank
    private String businessNumber;
    @NotBlank
    private String businessName;
}
