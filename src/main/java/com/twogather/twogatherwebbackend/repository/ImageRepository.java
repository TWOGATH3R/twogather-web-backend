package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("select i from Image i where i.store.storeId = :storeId")
    List<Image> findByStoreId(@Param("storeId") Long storeId);
}
