package com.twogather.twogatherwebbackend.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class StoreOwnerInfoResponse extends MemberInfo{
    String businessName;

    String businessNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate businessStartDate;

    public StoreOwnerInfoResponse(String name, String email, String phone, String businessNumber, String businessName, LocalDate businessStartDate){
        super(name,email,phone);
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.businessStartDate = businessStartDate;
    }
}
