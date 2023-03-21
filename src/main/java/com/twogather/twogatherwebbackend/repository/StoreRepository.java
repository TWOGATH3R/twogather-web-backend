package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);
}
