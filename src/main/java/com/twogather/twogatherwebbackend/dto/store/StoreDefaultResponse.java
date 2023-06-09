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
    private Long storeId;
    private String storeName;
    private String address;
    private String phone;
    private List<String> keywordList;
    private String categoryName;
    public StoreDefaultResponse(Long storeId, String storeName, String address, String phone,
                                String categoryName){
        this.storeId = storeId;
        this.storeName=storeName;
        this.address = address;
        this.phone =phone;
        this.categoryName=categoryName;
    }
    public void setKeywordList(List<String> keywordList){
        this.keywordList= keywordList;
    }
}
