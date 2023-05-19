package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreApprovalStatus;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)//Mock 객체 생성, 초기화
public class BusinessHourServiceTest {
    @Mock
    private BusinessHourRepository businessHourRepository;
    @Mock
    private StoreRepository storeRepository;

    private BusinessHourService businessHourService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        businessHourService = new BusinessHourService(businessHourRepository, storeRepository);
    }
    @Test
    @DisplayName("saveList: businessHour을 여러건 저장할때 사용한다. 입력되지않은 요일에 대해서는 영업안함을 표시하여 응답에 포함시킨다")
    void whenOnlyOpenDaysProvided_thenResponseIncludesClosedDays() {
        // given
        Store store = new Store(1l, "이름", "주소","010-1234-1234", StoreApprovalStatus.APPROVED,"");
        List<BusinessHourSaveRequest> onlyOpenDaysRequestList = Arrays.asList(
                new BusinessHourSaveRequest(1L, LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.MONDAY, true, false, null, null),
                new BusinessHourSaveRequest(1L, LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.TUESDAY, true, false, null, null),
                new BusinessHourSaveRequest(1L, LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.WEDNESDAY, true, false, null, null)
        );
        List<BusinessHour> fullWeekBusinessHours = Arrays.asList(
                new BusinessHour(1l,store,  LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.MONDAY, true, false, null, null),
                new BusinessHour(2l,store,  LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.TUESDAY, true, false, null, null),
                new BusinessHour(3l,store, LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.WEDNESDAY, true, false, null, null),
                new BusinessHour(4l,store, null,null, DayOfWeek.THURSDAY, false, false, null, null),
                new BusinessHour(5l,store,  null,null, DayOfWeek.FRIDAY, false, false, null, null),
                new BusinessHour(6l,store,null,null, DayOfWeek.SATURDAY, false, false, null, null),
                new BusinessHour(7l,store, null,null, DayOfWeek.SUNDAY, false, false, null, null)
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
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));
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
        Store store = new Store(1l, "이름", "주소","010-1234-1234", StoreApprovalStatus.APPROVED,"");
        List<BusinessHourSaveRequest> duplicatedDayOfWeekRequestList = Arrays.asList(
                new BusinessHourSaveRequest(1L, LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.MONDAY, true, false, null, null),
                new BusinessHourSaveRequest(1L, LocalTime.of(9, 0), LocalTime.of(18, 0), DayOfWeek.MONDAY, true, false, null, null)
        );

        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));

        // When & Then
        assertThrows(BusinessHourException.class, () -> {
            businessHourService.saveList(1L, duplicatedDayOfWeekRequestList);
        });
    }
}
