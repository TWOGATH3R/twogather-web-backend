package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.store.MyStoreResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreUpdateRequest;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    //TODO: isApproved, reasonForRejection 추가되었으니 아래 메서드 다시 작성
    public StoreResponse save(final StoreSaveRequest request){
        validateDuplicateName(request.getName());
        Store store = new Store(request.getName(), request.getAddress(), request.getPhone());
        Store savedStore = storeRepository.save(store);
        return toStoreResponse(savedStore);
    }

    public void delete(Long storeId) {
        Store store = findStore(storeId);
        storeRepository.delete(store);
    }

    public StoreResponse update(final Long storeId, final StoreUpdateRequest request) {
        Store store = findStore(storeId);
        if (request.getName() != null && !request.getName().isEmpty() && !request.getName().equals(store.getName())) {
            validateDuplicateName(request.getName());
            store.updateName(request.getName());
        }
        store.updateAddress(request.getAddress());
        store.updatePhone(request.getPhone());

        return toStoreResponse(store);
    }
    public List<StoreResponse> getStores(
          String categoryName, String keyword, int limit, int offset, String orderBy, String order){
        //TODO: 구현
        return new ArrayList<>();
    }
    public List<MyStoreResponse> getStoresByOwner(Long storeOwnerId, Integer limit, Integer offset){
        //TODO: 구현
        return new ArrayList<>();
    }
    public StoreResponse getStore(Long storeId){
        Store store = findStore(storeId);
        return toStoreResponse(store);
    }

    private void validateDuplicateName(String name){
        if (storeRepository.existsByName(name)) {
            throw new StoreException(StoreException.StoreErrorCode.DUPLICATE_NAME);
        }
    }
    private Store findStore(Long storeId){
        return storeRepository.findById(storeId).orElseThrow(() -> new StoreException(StoreException.StoreErrorCode.STORE_NOT_FOUND));
    }
    private StoreResponse toStoreResponse(Store store) {
        return new StoreResponse(store.getStoreId(), store.getName(), store.getAddress(), store.getPhone());
    }
}
