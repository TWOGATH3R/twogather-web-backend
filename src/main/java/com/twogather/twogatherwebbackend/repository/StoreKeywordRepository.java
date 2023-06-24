package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Keyword;
import com.twogather.twogatherwebbackend.domain.StoreKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreKeywordRepository extends JpaRepository<StoreKeyword, Long> {
    @Query("select sk from StoreKeyword sk where sk.store.storeId = :storeId")
    List<StoreKeyword> findByStoreId(@Param("storeId") Long storeId);
    @Modifying
    @Query("delete from StoreKeyword sk where sk.store.storeId = :storeId")
    void deleteByStoreId(@Param("storeId") Long storeId);
    @Query("SELECT k FROM StoreKeyword sk left join sk.keyword k WHERE sk.store.storeId = :storeId")
    List<Keyword> findKeywordsByStoreId(@Param("storeId") Long storeId);
}
