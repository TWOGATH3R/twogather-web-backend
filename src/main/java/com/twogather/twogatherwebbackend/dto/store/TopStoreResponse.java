package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopStoreResponse implements Serializable {
    private Long storeId;
    private String storeName;
    private String address;
    private Double avgScore;
    private String storeImageUrl;
    private Integer likeCount;
    public TopStoreResponse(Long storeId, String name, Double avgScore,
                            String address, String storeImageUrl, Integer likeCount){
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        this.avgScore = avgScore;
        this.storeImageUrl = storeImageUrl;
        this.likeCount = likeCount;
    }
}
