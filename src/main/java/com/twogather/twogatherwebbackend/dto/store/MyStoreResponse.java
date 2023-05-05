package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyStoreResponse {
    private Long storeId;
    private String name;
    private String address;
    private Boolean isApproved;
    private String reasonForRejection;
}
