package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopStoreResponse extends StoreBaseResponse{
    private Double avgScore;
    private String storeImageUrl;
    public TopStoreResponse(Long storeId, String name, Double avgScore, String address, String storeImageUrl){
        super(storeId,name,address);
        this.avgScore = avgScore;
        this.storeImageUrl = storeImageUrl;
    }
}
