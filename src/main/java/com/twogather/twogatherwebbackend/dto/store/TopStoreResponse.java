package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopStoreResponse{
    private Long storeId;
    private String storeName;
    private String address;
    private Double avgScore;
    private String storeImageUrl;
    public TopStoreResponse(Long storeId, String name, Double avgScore, String address, String storeImageUrl){
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        this.avgScore = avgScore;
        this.storeImageUrl = storeImageUrl;
    }
}
