package com.twogather.twogatherwebbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StoreOwnerSaveRequest extends UserSave {
    @Size(min = 10, max = 10, message = "숫자는 10자리여야 합니다.")
    @Digits(integer = 10,fraction = 0, message = "숫자로 이루어져야 합니다")
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String businessNumber;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String businessName;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$")
    private String businessStartDate;

    public StoreOwnerSaveRequest(String email, String loginPw, String name, String phone, String businessNumber, String businessName, String businessStartDate) {
        super(email, loginPw, name, phone);
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.businessStartDate = businessStartDate;
    }
}
