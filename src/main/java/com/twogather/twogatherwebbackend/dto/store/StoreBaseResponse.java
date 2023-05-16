package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
abstract public class StoreBaseResponse {
    protected Long storeId;
    protected String storeName;
    protected String address;
}
