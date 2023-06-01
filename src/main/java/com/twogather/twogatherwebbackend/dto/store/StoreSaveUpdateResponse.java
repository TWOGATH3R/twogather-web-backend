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

    public static StoreSaveUpdateResponse from(Long storeId, String name, String address, String phone){
        return new StoreSaveUpdateResponse(storeId,name,address,phone);
    }
}
