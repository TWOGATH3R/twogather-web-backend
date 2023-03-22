package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {
    List<BusinessHour> findByStoreStoreId(Long storeId);
    Optional<BusinessHour> findByStoreStoreIdAndDayOfWeek(Long storeId, DayOfWeek dayOfWeek);
    void deleteByStoreStoreIdAndDayOfWeek(Long storeId, DayOfWeek dayOfWeek);
}
