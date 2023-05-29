package com.twogather.twogatherwebbackend.dto.store;

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
public class StoreResponse extends StoreBaseResponse{
    private String phone;
    private String businessNumber;
    private String businessName;
    private LocalDate businessStartDate;

    public StoreResponse(Long storeId, String name, String phone, String address){
        super(storeId,name,address);
        this.phone = phone;
    }
}
