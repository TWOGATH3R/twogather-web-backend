package com.twogather.twogatherwebbackend.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyStoreResponse extends StoreBaseResponse {
    private String phone;
    private Boolean isApproved;
    private String reasonForRejection;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;
    private String storeImageUrl;

    public MyStoreResponse(Long storeId, String name, String address, String phone, Boolean isApproved, String reasonForRejection, LocalDate requestDate, String storeImageUrl){
        super(storeId,name,address);
        this.phone = phone;
        this.storeImageUrl = storeImageUrl;
        this.isApproved = isApproved;
        this.reasonForRejection = reasonForRejection;
        this.requestDate = requestDate;
    }
}
