package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {
    @Query("select b from BusinessHour b where b.store.storeId = :storeId")
    List<BusinessHour> findByStoreId(@Param("storeId") Long storeId);
}
