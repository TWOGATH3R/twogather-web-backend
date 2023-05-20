package com.twogather.twogatherwebbackend.repository.store;

import com.twogather.twogatherwebbackend.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreCustomRepository {
    boolean existsByName(String name);
    Optional<Store> findByName(String name);
}
