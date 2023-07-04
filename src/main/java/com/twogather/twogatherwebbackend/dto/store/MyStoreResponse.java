package com.twogather.twogatherwebbackend.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twogather.twogatherwebbackend.domain.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Builder
public class MyStoreResponse {
    private Long storeId;
    private String storeName;
    private String address;
    private String phone;
    private StoreStatus status;
    private String reasonForRejection;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;
    private String storeImageUrl;


    public MyStoreResponse(Long storeId, String name, String address, String phone, StoreStatus status, String reasonForRejection, LocalDate requestDate, String storeImageUrl) {
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        this.phone = phone;
        this.storeImageUrl = storeImageUrl;
        this.status = status;
        this.reasonForRejection = reasonForRejection;
        this.requestDate = requestDate;
    }
}
