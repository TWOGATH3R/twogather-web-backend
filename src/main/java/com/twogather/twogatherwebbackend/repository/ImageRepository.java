package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByStoreStoreId(Long storeId);
}
