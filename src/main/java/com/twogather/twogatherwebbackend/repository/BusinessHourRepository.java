package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {
    @Query("select m from BusinessHour m where m.store.storeId = :storeId")
    List<BusinessHour> findByStoreId(@Param("storeId") Long storeId);
    @Query("select b from BusinessHour b where b.store.storeId = :storeId and b.dayOfWeek = :dayOfWeek")
    Optional<BusinessHour> findByStoreIdAndDayOfWeek(@Param("storeId") Long storeId, @Param("dayOfWeek") DayOfWeek dayOfWeek);
}
