package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.BusinessHour;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateInfo;
import com.twogather.twogatherwebbackend.exception.*;
import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.valid.BusinessHourValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<BusinessHourResponse> findBusinessHoursByStoreId(Long storeId) {
        List<BusinessHour> businessHours = businessHourRepository.findByStoreId(storeId);
        if (businessHours.isEmpty()) {
            throw new BusinessHourException(NO_SUCH_BUSINESS_HOUR_BY_STORE_ID);
        }
        ArrayList<BusinessHourResponse> responses = new ArrayList<>();
        for (BusinessHour businessHour : businessHours) {
            responses.add(toBusinessHourResponse(businessHour));
        }
        return responses;
    }

    public List<BusinessHourResponse> setList(Long storeId, List<BusinessHourSaveUpdateInfo> requestList) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NO_SUCH_STORE));

        removeBusinessHour(store);

        Set<DayOfWeek> uniqueDays = checkDuplicateDays(requestList);
        List<BusinessHour> entityList = createBusinessHourListBySaveList(requestList, store, uniqueDays);

        List<BusinessHour> savedBusinessHourList = businessHourRepository.saveAll(entityList);

        return toBusinessHourResponseList(savedBusinessHourList);
    }

    private BusinessHour saveRequestToEntity(BusinessHourSaveUpdateInfo request, Store store) {
        BusinessHour businessHour =
                BusinessHour
                .builder()
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .dayOfWeek(request.getDayOfWeek())
                        .hasBreakTime(request.getHasBreakTime())
                        .breakEndTime(request.getBreakEndTime())
                        .breakStartTime(request.getBreakStartTime())
                        .isOpen(request.getIsOpen()).build();
        businessHour.addStore(store);
        return businessHour;
    }

    //동시성 문제가 일어날수도있으니 디비 unique제약조건
    private Set<DayOfWeek> checkDuplicateDays(List<BusinessHourSaveUpdateInfo> requestList) {
        Set<DayOfWeek> uniqueDays = new HashSet<>();
        for (BusinessHourSaveUpdateInfo request : requestList) {
            if (!uniqueDays.add((request).getDayOfWeek())) {
                throw new BusinessHourException(DUPLICATE_DAY_OF_WEEK);
            }
        }
        return uniqueDays;
    }

    private List<BusinessHour> createBusinessHourListBySaveList(List<BusinessHourSaveUpdateInfo> requestList, Store store, Set<DayOfWeek> uniqueDays) {
        List<BusinessHour> entityList = new ArrayList<>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (!uniqueDays.contains(dayOfWeek)) {
                entityList.add(createClosedBusinessHour(dayOfWeek, store));
            }
        }

        for (BusinessHourSaveUpdateInfo request : requestList) {
            validator.validateBusinessHourRequest(request);
            BusinessHour businessHour = saveRequestToEntity(request, store);
            entityList.add(businessHour);
        }
        return entityList;
    }

    private BusinessHour createClosedBusinessHour(DayOfWeek dayOfWeek, Store store) {
        BusinessHour businessHour = BusinessHour.builder().isOpen(false).dayOfWeek(dayOfWeek).hasBreakTime(false).build();
        businessHour.addStore(store);
        return businessHour;
    }

    private List<BusinessHourResponse> toBusinessHourResponseList(List<BusinessHour> businessHourList) {
        return businessHourList.stream().map(this::toBusinessHourResponse).collect(Collectors.toList());
    }

    private BusinessHourResponse toBusinessHourResponse(BusinessHour businessHour) {
        return new BusinessHourResponse(
                businessHour.getBusinessHourId(), businessHour.getStore().getStoreId(),
                businessHour.getStartTime(), businessHour.getEndTime(), businessHour.getDayOfWeek(), businessHour.getIsOpen(),
                businessHour.getHasBreakTime(), businessHour.getBreakStartTime(), businessHour.getBreakEndTime()
        );
    }
    private void removeBusinessHour(Store store){
        businessHourRepository.deleteAll(store.getBusinessHourList());
    }
}
