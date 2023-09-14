package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Keyword;
import com.twogather.twogatherwebbackend.domain.StoreKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreKeywordRepository extends JpaRepository<StoreKeyword, Long> {
    @Query("select sk from StoreKeyword sk INNER JOIN sk.store s where s.storeId = :storeId")
    List<StoreKeyword> findByStoreId(@Param("storeId") Long storeId);
    void deleteByStoreStoreId(Long storeId);
    @Query("select sk.keyword from StoreKeyword sk INNER JOIN sk.store s where s.storeId = :storeId")
    List<Keyword> findKeywordsByStoreId(@Param("storeId") Long storeId);
}
