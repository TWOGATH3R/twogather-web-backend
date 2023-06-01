package com.twogather.twogatherwebbackend.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreSaveUpdateRequest {
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String storeName;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    private String address;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;

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

}
