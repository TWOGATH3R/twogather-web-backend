package com.twogather.twogatherwebbackend.repository.store;

import com.twogather.twogatherwebbackend.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreCustomRepository {
    boolean existsByName(String name);
    Optional<Store> findByName(String name);

    @Query("select s from Store s where s.storeId = :storeId and s.isApproved = com.twogather.twogatherwebbackend.domain.StoreApprovalStatus.APPROVED")
    Optional<Store> findActiveStoreById(Long storeId);
}
