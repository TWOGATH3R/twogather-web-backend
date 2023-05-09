package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoresResponse {
    private Long storeId;
    private String name;
    private String address;
    private Double rating;
    private ArrayList<String> keywordList = new ArrayList<>();
    private String storeImageUrl;
}
