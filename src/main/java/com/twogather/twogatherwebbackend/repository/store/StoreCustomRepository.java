package com.twogather.twogatherwebbackend.repository.store;

import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.dto.store.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface StoreCustomRepository {
    List<TopStoreResponse> findTopNByType(int n, String order, String orderBy);
    Page<StoreResponseWithKeyword> findStoresByCondition(Pageable pageable, String categoryName, String keyword, String location, String storeName);
    Page<MyStoreResponse> findStoresByStatus(StoreStatus status, Pageable pageable);
    Page<MyStoreResponse> findMyStore(Long ownerId, Pageable pageable);
    Page<MyLikeStoreResponse> findMyLikeStore(Long memberId, Pageable pageable);
    Optional<StoreDefaultResponse> findDefaultActiveStoreInfo(Long storeId);
    Optional<StoreSaveUpdateResponse> findStoreDetail(Long storeId);
}
