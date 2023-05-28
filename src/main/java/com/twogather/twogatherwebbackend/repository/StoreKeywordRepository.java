package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.StoreKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreKeywordRepository extends JpaRepository<StoreKeyword, Long> {
    List<StoreKeyword> findByStoreStoreId(Long storeId);
}
