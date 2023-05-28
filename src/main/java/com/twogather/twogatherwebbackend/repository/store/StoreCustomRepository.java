package com.twogather.twogatherwebbackend.repository.store;

import com.twogather.twogatherwebbackend.dto.store.StoreResponseWithKeyword;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface StoreCustomRepository {
    List<TopStoreResponse> findTopNByType(int n, String order, String orderBy);
    Page<StoreResponseWithKeyword> findStoresByCondition(Pageable pageable, String categoryName, String keyword, String location);

}
