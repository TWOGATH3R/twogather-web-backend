package com.twogather.twogatherwebbackend.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class MyLikeStoreResponse {
    private Long storeId;
    private String storeName;
    private String address;
    private String phone;
    private String storeImageUrl;
    private List<String> keywordList;

    public MyLikeStoreResponse(Long storeId, String storeName, String address, String phone){
        this.storeId=storeId;
        this.storeName=storeName;
        this.address = address;
        this.phone = phone;
    }
    public void setKeywordList(List<String> keywordList){
        this.keywordList = keywordList;
    }
    public void setImageUrl(String url){
        this.storeImageUrl = url;
    }
}
