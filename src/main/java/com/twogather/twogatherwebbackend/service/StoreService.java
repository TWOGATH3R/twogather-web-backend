package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreUpdateRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreUpdateResponse;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;

    public StoreSaveResponse save(final StoreSaveRequest request){
        validateDuplicateName(request.getName());
        Store store = new Store(request.getName(), request.getAddress(), request.getPhone());
        Store storedStore = storeRepository.save(store);
        return new StoreSaveResponse(storedStore.getStoreId());
    }

    public void delete(Long storeId) {
        Store store = findStore(storeId);
        storeRepository.delete(store);
    }

    public StoreUpdateResponse update(final Long storeId, final StoreUpdateRequest request) {
        Store store = findStore(storeId);
        if (request.getName() != null && !request.getName().isEmpty() && !request.getName().equals(store.getName())) {
            validateDuplicateName(request.getName());
            store.updateName(request.getName());
        }
        store.updateAddress(request.getAddress());
        store.updatePhone(request.getPhone());

        return new StoreUpdateResponse(store.getStoreId());
    }

    private void validateDuplicateName(String name){
        if (storeRepository.existsByName(name)) {
            throw new StoreException(StoreException.StoreErrorCode.DUPLICATE_NAME);
        }
    }
    private Store findStore(Long storeId){
        return storeRepository.findById(storeId).orElseThrow(() -> new StoreException(StoreException.StoreErrorCode.STORE_NOT_FOUND));
    }
}
