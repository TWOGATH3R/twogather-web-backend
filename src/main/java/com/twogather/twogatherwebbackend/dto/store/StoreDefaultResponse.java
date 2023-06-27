package com.twogather.twogatherwebbackend.dto.store;

import com.twogather.twogatherwebbackend.domain.Store;
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
    private Integer likeCount;
    public StoreDefaultResponse(Long storeId, String storeName, String address, String phone,
                                String categoryName, Integer likeCount){
        this.storeId = storeId;
        this.storeName=storeName;
        this.address = address;
        this.phone =phone;
        this.categoryName=categoryName;
        this.likeCount = likeCount;
    }
    public void setKeywordList(List<String> keywordList){
        this.keywordList= keywordList;
    }

    public static StoreDefaultResponse of(Store store){
        return StoreDefaultResponse
                .builder()
                .storeId(store.getStoreId())
                .storeName(store.getName())
                .categoryName(store.getCategory().getName())
                .phone(store.getPhone())
                .address(store.getAddress())
                .likeCount(store.getLikesList().size()).build();
    }
}
