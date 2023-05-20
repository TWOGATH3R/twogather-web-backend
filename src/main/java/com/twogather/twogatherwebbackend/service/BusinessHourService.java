package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourUpdateRequest;
import com.twogather.twogatherwebbackend.exception.*;
import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.valid.BusinessHourValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.*;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BusinessHourService {
    private final BusinessHourRepository businessHourRepository;
    private final StoreRepository storeRepository;
    private final BusinessHourValidator validator;

    public List<BusinessHourResponse> saveList(Long storeId, List<BusinessHourSaveRequest> requestList){
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NO_SUCH_STORE));

        Set<DayOfWeek> uniqueDays = checkDuplicateDays(requestList);
        List<BusinessHour> entityList = createBusinessHourList(requestList, store, uniqueDays);

        List<BusinessHour> savedBusinessHourList = businessHourRepository.saveAll(entityList);

        return toBusinessHourResponseList(savedBusinessHourList);
    }

    public void deleteList(List<Long> businessHourIdList){
        for (Long id: businessHourIdList){
            delete(id);
        }
    }

    public List<BusinessHourResponse> findBusinessHoursByStoreId(Long storeId) {
        List<BusinessHour> businessHours = businessHourRepository.findByStoreStoreId(storeId);
        if (businessHours.isEmpty()) {
            throw new BusinessHourException(NO_SUCH_BUSINESS_HOUR_BY_STORE_ID);
        }
        ArrayList<BusinessHourResponse> responses = new ArrayList<>();
        for (BusinessHour businessHour: businessHours) {
            responses.add(toBusinessHourResponse(businessHour));
        }
        return responses;
    }

    public List<BusinessHourResponse> updateList(List<BusinessHourUpdateRequest> requestList){
        ArrayList<BusinessHourResponse> responseList = new ArrayList<>();
        for (BusinessHourUpdateRequest request: requestList){
            responseList.add(update(request));
        }
        return responseList;
    }
    public BusinessHourResponse update(BusinessHourUpdateRequest request){
        BusinessHour businessHour = findBusinessHour(request.getBusinessHourId());
        businessHour.update(request.getStartTime(), request.getEndTime(), request.getDayOfWeek(), request.getIsOpen(),
                request.getHasBreakTime(), request.getBreakStartTime(), request.getBreakEndTime());

        return toBusinessHourResponse(businessHour);
    }
    public void delete(Long businessHourId){
        try {
            businessHourRepository.deleteById(businessHourId);
        } catch (EmptyResultDataAccessException ex) {
            log.info("존재하지 않는 entity 입니다: businessHourId = {}", businessHourId);
            throw new BusinessHourException(NO_SUCH_BUSINESS_HOUR_BY_BUSINESS_HOUR_ID);
            // 엔티티가 존재하지 않는 경우 처리할 로직 작성
            // 예를 들어, 에러 로그 출력 또는 예외를 다시 던지는 등의 작업을 수행할 수 있습니다.
            // 예외를 던지지 않고 그냥 무시하고자 한다면 아무런 작업을 하지 않아도 됩니다.
        }
    }
    private Store findByStoreIdReturnStore(Long id){
        Optional<Store> store = storeRepository.findById(id);
        store.orElseThrow(()->new StoreException(NO_SUCH_STORE));
        return store.get();
    }
    private void createClosedBusinessHourList(Set<DayOfWeek> uniqueDays, Store store, List<BusinessHour> entityList){
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (!uniqueDays.contains(dayOfWeek)) {
                BusinessHour closedBusinessHour = createClosedBusinessHour(dayOfWeek, store);
                entityList.add(closedBusinessHour);
            }
        }
    }
    private void validateBusinessHourId(Long businessHourId){
        businessHourRepository.findById(businessHourId).orElseThrow(
                () -> new BusinessHourException(NO_SUCH_BUSINESS_HOUR_BY_BUSINESS_HOUR_ID)
        );
    }
    private BusinessHour findBusinessHour(Long businessHourId){
        Optional<BusinessHour> businessHour = businessHourRepository.findById(businessHourId);
        businessHour.orElseThrow(
                () -> new BusinessHourException(NO_SUCH_BUSINESS_HOUR_BY_BUSINESS_HOUR_ID)
        );
        return businessHour.get();
    }
    private void validateDuplicateDayOfWeek(Long storeId, DayOfWeek dayOfWeek) {
        Optional<BusinessHour> businessHourOptional = businessHourRepository.findByStoreStoreIdAndDayOfWeek(storeId, dayOfWeek);
        if (businessHourOptional.isPresent()) {
            throw new BusinessHourException(DUPLICATE_DAY_OF_WEEK);
        }
    }
    private BusinessHour toEntity(BusinessHourSaveRequest request, Store store){
        return new BusinessHour(store, request.getStartTime(), request.getEndTime(),
                request.getDayOfWeek(), request.getIsOpen(), request.getHasBreakTime(),
                request.getBreakStartTime(), request.getBreakEndTime());
    }

    private Set<DayOfWeek> checkDuplicateDays(List<BusinessHourSaveRequest> requestList) {
        Set<DayOfWeek> uniqueDays = new HashSet<>();
        for (BusinessHourSaveRequest request : requestList) {
            if (!uniqueDays.add(request.getDayOfWeek())) {
                throw new BusinessHourException(DUPLICATE_DAY_OF_WEEK);
            }
        }
        return uniqueDays;
    }

    private List<BusinessHour> createBusinessHourList(List<BusinessHourSaveRequest> requestList, Store store, Set<DayOfWeek> uniqueDays) {
        List<BusinessHour> entityList = new ArrayList<>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (!uniqueDays.contains(dayOfWeek)) {
                entityList.add(createClosedBusinessHour(dayOfWeek, store));
            }
        }

        for (BusinessHourSaveRequest request : requestList) {
            validator.validateBusinessHourRequest(request);
            BusinessHour businessHour = toEntity(request, store);
            entityList.add(businessHour);
        }

        return entityList;
    }

    private BusinessHour createClosedBusinessHour(DayOfWeek dayOfWeek, Store store) {
        return new BusinessHour(
                store, null, null, dayOfWeek, false,
                false, null, null
        );
    }

    private List<BusinessHourResponse> toBusinessHourResponseList(List<BusinessHour> businessHourList) {
        List<BusinessHourResponse> responseList = new ArrayList<>();
        for (BusinessHour businessHour : businessHourList) {
            responseList.add(toBusinessHourResponse(businessHour));
        }
        return responseList;
    }

    private BusinessHourResponse toBusinessHourResponse(BusinessHour businessHour) {
        return new BusinessHourResponse(
                businessHour.getBusinessHourId(), businessHour.getStore().getStoreId(),
                businessHour.getStartTime(), businessHour.getEndTime(), businessHour.getDayOfWeek(), businessHour.getIsOpen(),
                businessHour.getHasBreakTime(), businessHour.getBreakStartTime(), businessHour.getBreakEndTime()
        );
    }
}
