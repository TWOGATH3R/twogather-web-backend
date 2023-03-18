package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.StoreOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreOwnerRepository extends JpaRepository<StoreOwner, Long> {
    boolean existsByEmail(String email);
}
