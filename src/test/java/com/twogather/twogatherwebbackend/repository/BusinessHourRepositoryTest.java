package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public class BusinessHourRepositoryTest extends RepositoryTest {


    @BeforeEach
    void init(){
        createStore();
        createBusinessHour(store);
    }

    @Test
    public void findByStoreId(){
        List<BusinessHour> list = businessHourRepository.findByStoreId(store.getStoreId());
    }
    @Test
    public void findByStoreIdAndDayOfWeek(){
        Optional<BusinessHour> list = businessHourRepository.findByStoreIdAndDayOfWeek(store.getStoreId(), DayOfWeek.MONDAY);
    }

    public void createBusinessHour(Store store){
        businessHourRepository.save(BusinessHour.builder().dayOfWeek(DayOfWeek.MONDAY).store(store).isOpen(true).build());
        businessHourRepository.save(BusinessHour.builder().dayOfWeek(DayOfWeek.THURSDAY).store(store).isOpen(true).build());
        businessHourRepository.save(BusinessHour.builder().dayOfWeek(DayOfWeek.FRIDAY).store(store).isOpen(true).build());
        businessHourRepository.save(BusinessHour.builder().dayOfWeek(DayOfWeek.SATURDAY).store(store).isOpen(false).build());
    }
}
