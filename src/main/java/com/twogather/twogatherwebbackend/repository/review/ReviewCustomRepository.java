package com.twogather.twogatherwebbackend.repository.review;

public interface ReviewCustomRepository {
    Double findAvgScoreByStoreId(Long storeId);
}
