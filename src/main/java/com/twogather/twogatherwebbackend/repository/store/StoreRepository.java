package com.twogather.twogatherwebbackend.repository.store;

import com.twogather.twogatherwebbackend.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreCustomRepository {
    boolean existsByName(String name);
}
