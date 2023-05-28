package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.StoreOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreOwnerRepository extends JpaRepository<StoreOwner, Long> {
    boolean existsByUsername(String username);
    Optional<StoreOwner> findByUsername(String username);
}
