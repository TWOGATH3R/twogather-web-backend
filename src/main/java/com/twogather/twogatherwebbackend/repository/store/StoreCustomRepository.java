package com.twogather.twogatherwebbackend.repository.store;

import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;

import java.util.List;

public interface StoreCustomRepository {
    List<TopStoreResponse> findTopNByScore(int n);
    List<TopStoreResponse> findTopNByReviewCount(int n);
}
