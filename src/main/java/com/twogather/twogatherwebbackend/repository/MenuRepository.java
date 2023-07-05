package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query("select m from Menu m where m.store.storeId = :storeId")
    List<Menu> findByStoreId(@Param("storeId") Long storeId);
    @Query("select m from Menu m where m.store.storeId = :storeId and m.menuId = :menuId")
    Optional<Menu> findByStoreIdAndMenuId(@Param("storeId") Long storeId, @Param("menuId") Long menuId);
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Menu m WHERE m.store.storeId = :storeId AND m.name = :menuName")
    boolean existsByStoreIdAndName(@Param("storeId") Long storeId, @Param("menuName") String menuName);
}
