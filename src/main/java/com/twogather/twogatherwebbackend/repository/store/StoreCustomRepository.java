package com.twogather.twogatherwebbackend.repository.store;

import com.twogather.twogatherwebbackend.domain.Category;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;

import java.util.List;
import java.util.Optional;

public interface StoreCustomRepository {
    List<TopStoreResponse> findTopNByScore(int n);
    List<TopStoreResponse> findTopNByReviewCount(int n);
    Optional<Category> findCategory(Long storeId);
}
