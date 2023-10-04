package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@Builder
public class StoreResponseWithKeyword {
    private Long storeId;
    private String storeName;
    private String address;
    private Double avgScore;
    private List<String> keywordList = new ArrayList<>();
    private String storeImageUrl;
    private Long likeCount;

    public StoreResponseWithKeyword(Long storeId, String name, String address, Double score,
                                    Long likeCount){
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        storeImageUrl = "";
        keywordList = new ArrayList<>();
        this.avgScore = score;
        this.likeCount = likeCount;
    }
    public StoreResponseWithKeyword(Long storeId, String name, String address, Double score,
                                    List<String> keywordList, String storeImageUrl,
                                    Long likeCount){
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        this.storeImageUrl = storeImageUrl;
        this.keywordList = keywordList;
        this.avgScore = score;
        this.likeCount = likeCount;

    }

    public void setKeywordList(List<String> keywordList) {
        this.keywordList = keywordList;
    }
    public void setStoreImageUrl(String imageUrl){
        this.storeImageUrl = imageUrl;
    }
}
