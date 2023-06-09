package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
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
    @DisplayName("saveList: businessHour을 여러건 저장할때 사용한다. 입력되지않은 요일에 대해서는 영업안함을 표시하여 응답에 포함시킨다")
    void whenOnlyOpenDaysProvided_thenResponseIncludesClosedDays() {
        // given
        List<BusinessHourSaveUpdateInfo> onlyOpenDaysRequestList = Arrays.asList(
                new BusinessHourSaveUpdateInfo( LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.MONDAY, true, false, null, null),
                new BusinessHourSaveUpdateInfo( LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.TUESDAY, true, false, null, null),
                new BusinessHourSaveUpdateInfo( LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.WEDNESDAY, true, false, null, null)
        );
        List<BusinessHour> fullWeekBusinessHours = Arrays.asList(
                new BusinessHour(1l,APPROVED_STORE,  LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.MONDAY, true, false, null, null),
                new BusinessHour(2l,APPROVED_STORE,  LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.TUESDAY, true, false, null, null),
                new BusinessHour(3l,APPROVED_STORE, LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.WEDNESDAY, true, false, null, null),
                new BusinessHour(4l,APPROVED_STORE, null,null, DayOfWeek.THURSDAY, false, false, null, null),
                new BusinessHour(5l,APPROVED_STORE,  null,null, DayOfWeek.FRIDAY, false, false, null, null),
                new BusinessHour(6l,APPROVED_STORE,null,null, DayOfWeek.SATURDAY, false, false, null, null),
                new BusinessHour(7l,APPROVED_STORE, null,null, DayOfWeek.SUNDAY, false, false, null, null)
        );
        List<BusinessHourResponse> fullWeekBusinessHourResponse = Arrays.asList(
                new BusinessHourResponse(1l,1l,  LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.MONDAY, true, false, null, null),
                new BusinessHourResponse(2l,1l,  LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.TUESDAY, true, false, null, null),
                new BusinessHourResponse(3l,1l, LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.WEDNESDAY, true, false, null, null),
                new BusinessHourResponse(4l,1l, null,null, DayOfWeek.THURSDAY, false, false, null, null),
                new BusinessHourResponse(5l,1l,  null,null, DayOfWeek.FRIDAY, false, false, null, null),
                new BusinessHourResponse(6l,1l,null,null, DayOfWeek.SATURDAY, false, false, null, null),
                new BusinessHourResponse(7l,1l, null,null, DayOfWeek.SUNDAY, false, false, null, null)
        );


        // when
        doNothing().when(validator).validateBusinessHourRequest(any());
        when(storeRepository.findAllStoreById(anyLong())).thenReturn(Optional.of(APPROVED_STORE));
        when(businessHourRepository.saveAll(any())).thenReturn(fullWeekBusinessHours);

        List<BusinessHourResponse> responseList = businessHourService.saveList(1L, onlyOpenDaysRequestList);

        // then
        assertNotNull(responseList);
        assertEquals(7, responseList.size());
        assertThat(responseList).usingRecursiveComparison().isEqualTo(fullWeekBusinessHourResponse);
    }


    @Test
    @DisplayName("saveList: businessHour 을 여러건 저장할때 사용한다. 요일이 중복되어 요청되는 경우 exception")
    void whenDuplicateDaysProvided_thenThrowsException() {
        // Given
       List<BusinessHourSaveUpdateInfo> duplicatedDayOfWeekRequestList = Arrays.asList(
                new BusinessHourSaveUpdateInfo( LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.MONDAY, true, false, null, null),
                new BusinessHourSaveUpdateInfo( LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.MONDAY, true, false, null, null)
        );

        when(storeRepository.findAllStoreById(anyLong())).thenReturn(Optional.of(APPROVED_STORE));

        // When & Then
        assertThrows(BusinessHourException.class, () -> {
            businessHourService.saveList(1L, duplicatedDayOfWeekRequestList);
        });
    }
}
