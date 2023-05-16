package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)//Mock 객체 생성, 초기화
public class BusinessHourServiceTest {
    @Mock
    private BusinessHourRepository businessHourRepository;
    @Mock
    private StoreRepository storeRepository;
    private BusinessHourService businessHourService;


    /*
    @Test1
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


    @Test1
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

    @Test1
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
    @Test1
    @DisplayName("find: 유효한 요청이 왔을때 유효한 응답을 반환한다")
    public void find_WhenValidRequest_ShouldResponse(){
        //given
        final List<BusinessHour> result = returnBusinessHourList();
        final Long ID = 1l;
        when(businessHourRepository.findByStoreStoreId(ID)).thenReturn(result);
        //when
        List<BusinessHourResponse> list = businessHourService.findBusinessHoursByStoreId(ID);
        //then
        assertEquals(list.get(0), result.get(0));
    }
    @Test1
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
    @Test1
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
    @Test1
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
    @Test1
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
        return new BusinessHourSaveRequest(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null, null);
    }
    private BusinessHourUpdateRequest returnBusinessHourUpdateRequest(){
        return new BusinessHourUpdateRequest(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, true, BREAK_START_TIME, BREAK_END_TIME);
    }
    private BusinessHour returnBusinessHour(){
        return new BusinessHour(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);
    }
    private List<BusinessHour> returnBusinessHourList(){
        List<BusinessHour> list = new ArrayList<>();
        list.add(new BusinessHour(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null));
        list.add(new BusinessHour(ANOTHER_STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null, null));
        return list;
    }*/
}
