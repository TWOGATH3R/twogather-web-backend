package com.twogather.twogatherwebbackend.dto.store;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopStoreInfoPreviewResponse {
    private List<TopStoreInfoResponse> popularStores;
    private List<TopStoreInfoResponse> topRatedStores;
}
