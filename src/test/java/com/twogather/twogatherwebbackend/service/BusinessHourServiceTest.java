package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateInfo;
import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.valid.BusinessHourValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.twogather.twogatherwebbackend.util.TestConstants.APPROVED_STORE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)//Mock 객체 생성, 초기화
public class BusinessHourServiceTest {
    @Mock
    private BusinessHourRepository businessHourRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private BusinessHourValidator validator;

    private BusinessHourService businessHourService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        businessHourService = new BusinessHourService(businessHourRepository, storeRepository, validator);
    }

    @Test
    @DisplayName("연관관계 메서드 테스트")
    @Transactional
    void whenUseAddStore_thenSuccessInjection() {
        // Given
        Store store = Store.builder().name("가게1").build();
        BusinessHour businessHour = BusinessHour.builder().build();
        //when

        businessHour.addStore(store);

        //then
        assertEquals(store, businessHour.getStore());

    }

    @Test
    @DisplayName("연관관계 메서드는 두번 연속으로 store이 주입되더라도 마지막 으로 넣은 개체와 연관관계를 가지고 있어야한다")
    @Transactional
    void whenUseAddStoreBy2Change_thenSuccessInjection() {
        // Given
        Store store1 = Store.builder().name("가게1").build();
        Store store2 = Store.builder().name("가게2").build();
        BusinessHour businessHour = BusinessHour.builder().build();
        //when
        businessHour.addStore(store1);
        businessHour.addStore(store2);

        //then
        assertEquals(store2, businessHour.getStore());

    }

}
