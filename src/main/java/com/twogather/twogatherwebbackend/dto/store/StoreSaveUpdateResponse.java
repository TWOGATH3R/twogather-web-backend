package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreSaveUpdateResponse {
    private Long storeId;
    private String storeName;
    private String address;
    private String phone;
}
