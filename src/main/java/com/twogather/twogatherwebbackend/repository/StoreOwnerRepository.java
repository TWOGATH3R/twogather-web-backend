package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.StoreOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreOwnerRepository extends JpaRepository<StoreOwner, Long> {
    boolean existsByEmail(String email);
}
