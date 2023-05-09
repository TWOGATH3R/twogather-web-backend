package com.twogather.twogatherwebbackend.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyStoreResponse {
    private Long storeId;
    private String storeName;
    private String address;
    private String storePhone;
    private Boolean isApproved;
    private String reasonForRejection;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;
    private String storeImageUrl;
}
