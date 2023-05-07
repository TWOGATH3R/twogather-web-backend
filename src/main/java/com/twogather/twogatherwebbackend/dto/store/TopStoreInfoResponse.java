package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopStoreInfoResponse {
    private String storeName;
    private Double score;
    private String address;
    private String storeImageUrl;
}
