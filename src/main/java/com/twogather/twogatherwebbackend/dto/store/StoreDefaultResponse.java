package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class StoreDefaultResponse {
    private Long ownerId;
    private Long storeId;
    private String storeName;
    private String address;
    private String phone;
    private List<String> keywordList;
    private String categoryName;
    private Double avgScore;
    private Integer likeCount;
    public StoreDefaultResponse(Long ownerId, Long storeId, String storeName, String address, String phone,
                                String categoryName, Integer likeCount, Double avgScore){
        this.ownerId = ownerId;
        this.storeId = storeId;
        this.storeName=storeName;
        this.address = address;
        this.phone =phone;
        this.categoryName=categoryName;
        this.likeCount = likeCount;
        this.avgScore = avgScore;
    }
    public void setKeywordList(List<String> keywordList){
        if(keywordList!=null){
            this.keywordList= keywordList;
        }

    }
}
