package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreResponseWithKeyword extends StoreBaseResponse {
    private Double avgScore;
    private List<String> keywordList = new ArrayList<>();
    private String storeImageUrl;


    public StoreResponseWithKeyword(Long storeId, String name, String address, Double score, List<String> keywordList, String storeImageUrl){
        super(storeId,name,address);
        this.storeImageUrl = storeImageUrl;
        this.keywordList = keywordList;
        this.avgScore = score;

    }
}
