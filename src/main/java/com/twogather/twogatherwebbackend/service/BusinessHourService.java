package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateRequest;
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

    public List<BusinessHourResponse> saveList(Long storeId, List<BusinessHourSaveUpdateRequest> requestList){
        Store store = storeRepository.findActiveStoreById(storeId)
                .orElseThrow(() -> new StoreException(NO_SUCH_STORE));

        Set<DayOfWeek> uniqueDays = checkDuplicateDays(requestList);
        List<BusinessHour> entityList = createBusinessHourListBySaveList(requestList, store, uniqueDays);

        List<BusinessHour> savedBusinessHourList = businessHourRepository.saveAll(entityList);

        return toBusinessHourResponseList(savedBusinessHourList);
    }

    public void deleteList(List<Long> businessHourIdList){
        for (Long id: businessHourIdList){
            delete(id);
        }
    }

    @Transactional(readOnly = true)
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

    public List<BusinessHourResponse> updateList(Long storeId, List<BusinessHourSaveUpdateRequest> requestList){
        Store store = storeRepository.findActiveStoreById(storeId)
                .orElseThrow(() -> new StoreException(NO_SUCH_STORE));

        checkDuplicateDays(requestList);

        createBusinessHourListByUpdateList(requestList, store);
        List<BusinessHour> businessHourList = businessHourRepository.findByStoreStoreId(storeId);

        if(businessHourList.size()!=7){
            throw new BusinessHourException(INVALID_BUSINESS_HOUR);
        }

        ArrayList<BusinessHourResponse> responseList = new ArrayList<>();
        for (BusinessHour businessHour: businessHourList){
            responseList.add(toBusinessHourResponse(businessHour));
        }
        return responseList;
    }
    public void delete(Long businessHourId){
        try {
            businessHourRepository.deleteById(businessHourId);
        } catch (EmptyResultDataAccessException ex) {
            log.info("존재하지 않는 entity 입니다: businessHourId = {}", businessHourId);
            throw new BusinessHourException(NO_SUCH_BUSINESS_HOUR_BY_BUSINESS_HOUR_ID);
        }
    }
    private BusinessHour saveRequestToEntity(BusinessHourSaveUpdateRequest request, Store store){
        return new BusinessHour(store, request.getStartTime(), request.getEndTime(),
                request.getDayOfWeek(), request.getIsOpen(), request.getHasBreakTime(),
                request.getBreakStartTime(), request.getBreakEndTime());
    }

    private Set<DayOfWeek> checkDuplicateDays(List<BusinessHourSaveUpdateRequest> requestList) {
        Set<DayOfWeek> uniqueDays = new HashSet<>();
        for (BusinessHourSaveUpdateRequest request : requestList) {
            if (!uniqueDays.add((request).getDayOfWeek())) {
                throw new BusinessHourException(DUPLICATE_DAY_OF_WEEK);
            }
        }
        return uniqueDays;
    }

    private List<BusinessHour> createBusinessHourListBySaveList(List<BusinessHourSaveUpdateRequest> requestList, Store store, Set<DayOfWeek> uniqueDays) {
        List<BusinessHour> entityList = new ArrayList<>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (!uniqueDays.contains(dayOfWeek)) {
                entityList.add(createClosedBusinessHour(dayOfWeek, store));
            }
        }

        for (BusinessHourSaveUpdateRequest request : requestList) {
            validator.validateBusinessHourRequest(request);
            BusinessHour businessHour = saveRequestToEntity(request, store);
            entityList.add(businessHour);
        }

        return entityList;
    }
    private void createBusinessHourListByUpdateList(List<BusinessHourSaveUpdateRequest> requestList, Store store) {

        for (BusinessHourSaveUpdateRequest request : requestList) {
            validator.validateBusinessHourRequest(request);
            BusinessHour businessHour = businessHourRepository.findByStoreStoreIdAndDayOfWeek(store.getStoreId(), request.getDayOfWeek())
                    .orElseThrow(
                            () -> new BusinessHourException(NO_SUCH_BUSINESS_HOUR_BY_STORE_ID)
                    );
            businessHour.update(request.getStartTime(), request.getEndTime(), request.getDayOfWeek(),
                    request.getIsOpen(), request.getHasBreakTime(), request.getBreakStartTime(), request.getBreakEndTime());

        }
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
