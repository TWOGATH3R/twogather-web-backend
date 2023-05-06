package com.twogather.twogatherwebbackend.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class StoreOwnerResponse extends MemberResponse {
    String businessName;
    String businessNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate businessStartDate;

    public StoreOwnerResponse(Long id, String name, String email, String businessNumber, String businessName, LocalDate businessStartDate){
        super(id, email,name);
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.businessStartDate = businessStartDate;
    }
}
