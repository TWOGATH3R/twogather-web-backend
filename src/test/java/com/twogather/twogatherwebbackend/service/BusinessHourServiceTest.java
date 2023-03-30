package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourUpdateRequest;
import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//Mock 객체 생성, 초기화
public class BusinessHourServiceTest {
    @Mock
    private BusinessHourRepository businessHourRepository;
    @Mock
    private StoreRepository storeRepository;
    private BusinessHourService businessHourService;

    @BeforeEach
    void setUp() {
        businessHourService = new BusinessHourService(businessHourRepository, storeRepository);
    }

    @Test
    @DisplayName("save: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void save_WhenValidRequest_ShouldResponse() {
        // given
        final Long ID = 1L;
        final BusinessHour businessHour = returnBusinessHour();
        when(storeRepository.findById(ID)).thenReturn(Optional.of(new Store()));
        when(businessHourRepository.save(any(BusinessHour.class))).thenReturn(businessHour);
        BusinessHourSaveRequest request = returnBusinessHourSaveRequest();
        when(businessHourRepository.findByStoreStoreIdAndDayOfWeek(request.getStoreId(), request.getDayOfWeek()))
                .thenReturn(Optional.empty());
        // when
        // then
        assertDoesNotThrow(() -> businessHourService.save(request));
    }


    @Test
    @DisplayName("save: 저장하는 과정에서 <가게 ID + 요일> 에 해당하는 객체가 발견되어 예외가 터진다")
    public void save_WhenFoundAnotherStoreEntity_ShouldReturnException() {
        // given
        BusinessHourSaveRequest request = returnBusinessHourSaveRequest();
        when(businessHourRepository.findByStoreStoreIdAndDayOfWeek(request.getStoreId(), request.getDayOfWeek()))
                .thenReturn(Optional.of(new BusinessHour()));
        // when, then
        BusinessHourException exception = assertThrows(BusinessHourException.class, () -> {
            businessHourService.save(request);
        });
        assertEquals(BusinessHourException.BusinessHourErrorCode.DUPLICATE_DAY_OF_WEEK, exception.getErrorCode());
    }

    @Test
    @DisplayName("find: storeId에 해당하는 businessHour 객체가 존재하지 않는다")
    public void find_WhenStoreIdNotFound_ShouldThrowException(){
        //given
        final Long ID = 1L;
        when(businessHourRepository.findByStoreStoreId(ID)).thenReturn(new ArrayList<>());
        //when
        BusinessHourException exception = assertThrows(BusinessHourException.class, () -> {
            businessHourService.findBusinessHoursByStoreId(ID);
        });
        //then
        assertEquals(BusinessHourException.BusinessHourErrorCode.NO_SUCH_BUSINESS_HOUR_BY_STORE_ID, exception.getErrorCode());
    }
    @Test
    @DisplayName("find: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void find_WhenValidRequest_ShouldResponse(){
        //given
        final List<BusinessHour> result = returnBusinessHourList();
        final Long ID = 1l;
        when(businessHourRepository.findByStoreStoreId(ID)).thenReturn(result);
        //when
        List<BusinessHour> list = businessHourService.findBusinessHoursByStoreId(ID);
        //then
        assertEquals(list.get(0), result.get(0));
    }
    @Test
    @DisplayName("update: storeId와 dayOfWeek(요일)에 해당하는 BusinessHour객체가 없습니다")
    public void update_WhenNoSuchBusinessHour_ShouldReturnException(){
        //given
        final Long ID = 1l;
        when(businessHourRepository.findById(ID)).thenReturn(Optional.empty());
        BusinessHourUpdateRequest request = returnBusinessHourUpdateRequest();
        //when
        BusinessHourException exception = assertThrows(BusinessHourException.class, () -> {
            businessHourService.update(ID, request);
        });
        //then
        assertEquals(BusinessHourException.BusinessHourErrorCode.NO_SUCH_BUSINESS_HOUR_BY_BUSINESS_HOUR_ID, exception.getErrorCode());
    }
    @Test
    @DisplayName("update: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void update_WhenValidRequest_ShouldResponse(){
        //given
        final Long ID = 1l;
        BusinessHour originBusinessHour = returnBusinessHour();
        when(businessHourRepository.findById(ID)).thenReturn(Optional.of(originBusinessHour));
        BusinessHourUpdateRequest request = returnBusinessHourUpdateRequest();
        //when
        businessHourService.update(ID, request);
        BusinessHour updatedBusinessHour = businessHourRepository.findById(ID).get();
        //then
        assertEquals(updatedBusinessHour.getEndTime(), request.getEndTime());
        assertEquals(updatedBusinessHour.getStartTime(), request.getStartTime());
        assertEquals(updatedBusinessHour.getDayOfWeek(), request.getDayOfWeek());

    }
    @Test
    @DisplayName("delete: businessHour id로 찾은 객체가 존재하지 않습니다")
    public void delete_WhenNoSuchBusinessHour_ShouldReturnException(){
        //given
        final Long ID = 1l;
        when(businessHourRepository.findById(ID)).thenReturn(Optional.empty());
        //when
        BusinessHourException exception = assertThrows(BusinessHourException.class, () -> {
            businessHourService.delete(ID);
        });
        //then
        assertEquals(BusinessHourException.BusinessHourErrorCode.NO_SUCH_BUSINESS_HOUR_BY_BUSINESS_HOUR_ID, exception.getErrorCode());
    }
    @Test
    @DisplayName("delete: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void delete_WhenValidRequest_ShouldResponse(){
        //given
        final Long ID = 1l;
        when(businessHourRepository.findById(ID)).thenReturn(Optional.of(new BusinessHour()));
        doNothing().when(businessHourRepository).deleteById(ID);
        //when, then
        assertDoesNotThrow(() -> businessHourService.delete(ID));
    }
    private BusinessHourSaveRequest returnBusinessHourSaveRequest(){
        return new BusinessHourSaveRequest(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN);
    }
    private BusinessHourUpdateRequest returnBusinessHourUpdateRequest(){
        return new BusinessHourUpdateRequest(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN);
    }
    private BusinessHour returnBusinessHour(){
        return new BusinessHour(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN);
    }
    private List<BusinessHour> returnBusinessHourList(){
        List<BusinessHour> list = new ArrayList<>();
        list.add(new BusinessHour(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN));
        list.add(new BusinessHour(ANOTHER_STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN));
        return list;
    }
}
