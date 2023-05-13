package com.twogather.twogatherwebbackend.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twogather.twogatherwebbackend.dto.valid.BizRegNumberValidation;
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
@BizRegNumberValidation
public class StoreOwnerSaveUpdateRequest extends MemberSaveUpdateRequest {
    private String businessNumber;

    private String businessName;
    private LocalDate businessStartDate;

    public StoreOwnerSaveUpdateRequest(String email, String password, String name, String businessNumber, String businessName, LocalDate businessStartDate) {
        super(email, password, name);
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.businessStartDate = businessStartDate;
    }
}
