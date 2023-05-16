package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StoreResponse extends StoreBaseResponse{
    private String phone;
    public StoreResponse(Long storeId, String name, String phone, String address, String storeImageUrl){
        super(storeId,name,address);
        this.phone = phone;
    }
}
