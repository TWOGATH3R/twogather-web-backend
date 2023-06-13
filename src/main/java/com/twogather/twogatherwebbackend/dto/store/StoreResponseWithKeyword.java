package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class StoreResponseWithKeyword {
    private Long storeId;
    private String storeName;
    private String address;
    private Double avgScore;
    private List<String> keywordList = new ArrayList<>();
    private String storeImageUrl;


    public StoreResponseWithKeyword(Long storeId, String name, String address, Double score, List<String> keywordList, String storeImageUrl){
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        this.storeImageUrl = storeImageUrl;
        this.keywordList = keywordList;
        this.avgScore = score;

    }
}
