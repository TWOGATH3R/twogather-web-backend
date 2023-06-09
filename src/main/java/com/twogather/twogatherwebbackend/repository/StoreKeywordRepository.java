package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Keyword;
import com.twogather.twogatherwebbackend.domain.StoreKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreKeywordRepository extends JpaRepository<StoreKeyword, Long> {
    List<StoreKeyword> findByStoreStoreId(Long storeId);
    void deleteByStoreStoreId(Long storeId);
    @Query("SELECT sk.keyword FROM StoreKeyword sk WHERE sk.store.storeId = :storeId")
    List<Keyword> findKeywordsByStoreId(@Param("storeId") Long storeId);
}
