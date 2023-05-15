package com.twogather.twogatherwebbackend.repository.store;

import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.store.TopStoreInfoResponse;

import java.util.List;

public interface StoreCustomRepository {
    List<TopStoreInfoResponse> findTopNByScore(int n);
    List<TopStoreInfoResponse> findTopNByReviewCount(int n);
}
