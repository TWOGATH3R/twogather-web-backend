package com.twogather.twogatherwebbackend.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StoreOwnerSaveUpdateRequest extends MemberSaveUpdateRequest {
    @Size(min = 10, max = 10, message = "숫자는 10자리여야 합니다.")
    @Digits(integer = 10,fraction = 0, message = "숫자로 이루어져야 합니다")
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String businessNumber;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String businessName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalDate businessStartDate;

    public StoreOwnerSaveUpdateRequest(String email, String password, String name, String businessNumber, String businessName, LocalDate businessStartDate) {
        super(email, password, name);
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.businessStartDate = businessStartDate;
    }
}
