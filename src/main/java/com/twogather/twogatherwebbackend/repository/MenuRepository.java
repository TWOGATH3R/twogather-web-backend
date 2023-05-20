package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStoreStoreId(Long storeId);
    Optional<Menu> findByStoreStoreIdAndMenuId(Long storeId, Long menuId);
}
