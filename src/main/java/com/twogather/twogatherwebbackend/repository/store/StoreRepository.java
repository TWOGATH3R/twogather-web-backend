package com.twogather.twogatherwebbackend.repository.store;

import com.twogather.twogatherwebbackend.domain.Category;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreCustomRepository {
    boolean existsByName(String name);
    Optional<Store> findByName(String name);
    @Query("select s from Store s where s.storeId = :id and s.status = com.twogather.twogatherwebbackend.domain.StoreStatus.APPROVED ")
    Optional<Store> findActiveStoreById(@Param("id") Long id);
    @Query("select s from Store s where s.storeId = :id and s.status <> com.twogather.twogatherwebbackend.domain.StoreStatus.DELETED ")
    Optional<Store> findAllStoreById(@Param("id") Long id);
}
