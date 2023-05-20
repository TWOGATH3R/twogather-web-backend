package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.StoreType;
import com.twogather.twogatherwebbackend.dto.store.*;
import com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException.AccessDeniedExceptionErrorCode.ACCESS_DENIED;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_EMAIL;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.INVALID_STORE_TYPE;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final StoreOwnerRepository storeOwnerRepository;
    //TODO: isApproved, reasonForRejection 추가되었으니 아래 메서드 다시 작성

    public StoreSaveUpdateResponse save(final StoreSaveUpdateRequest request){
        String email = SecurityUtils.getLoginUserEmail();
        StoreOwner owner = storeOwnerRepository.findByEmail(email).orElseThrow(
                ()->new MemberException(NO_SUCH_EMAIL)
        );
        validateDuplicateName(request.getStoreName());
        Store store = new Store(owner, request.getStoreName(), request.getAddress(), request.getPhone());
        Store savedStore = storeRepository.save(store);
        return toStoreSaveUpdateResponse(savedStore);
    }
    public boolean isMyStore(Long storeId) {
        String email = SecurityUtils.getLoginUserEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(
                ()-> new MemberException(NO_SUCH_EMAIL)
        );
        Store store = storeRepository.findById(storeId).orElseThrow(()->
                new CustomAccessDeniedException(ACCESS_DENIED)
        );
        if (!store.getOwner().getMemberId().equals(member.getMemberId())) {
            throw new CustomAccessDeniedException(ACCESS_DENIED);
        }
        return true;
    }
    public List<String> getKeyword(){
        //TODO: 구현
        return null;
    }
    public List<TopStoreResponse> getStoresTopN(StoreType type, int n){
        if (type.equals(StoreType.MOST_REVIEWED)){
            return storeRepository.findTopNByReviewCount(n);
        }else if(type.equals(StoreType.TOP_RATED)){
            return storeRepository.findTopNByScore(n);
        }
        throw new StoreException(INVALID_STORE_TYPE);
    }

    public void delete(Long storeId) {
        Store store = findStore(storeId);
        storeRepository.delete(store);
    }

    public StoreSaveUpdateResponse update(final Long storeId, final StoreSaveUpdateRequest request) {
        Store store = findStore(storeId);
        if (request.getStoreName() != null && !request.getStoreName().isEmpty() && !request.getStoreName().equals(store.getName())) {
            validateDuplicateName(request.getStoreName());
            store.updateName(request.getStoreName());
        }
        store.updateAddress(request.getAddress());
        store.updatePhone(request.getPhone());

        return toStoreSaveUpdateResponse(store);
    }
    public List<StoreResponseWithKeyword> getStores(
          String categoryName, String keyword, int limit, int offset, String orderBy, String order, String location){
        //TODO: 구현
        return new ArrayList<>();
    }
    public Page<MyStoreResponse> getStoresByOwner(Long storeOwnerId, Integer limit, Integer offset){
        //TODO: 구현
        return null;
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
        return storeRepository.findById(storeId).orElseThrow(() -> new StoreException(NO_SUCH_STORE));
    }
    private StoreResponse toStoreResponse(Store store){
        //TODO: 구현
        return null;
    }
    private StoreSaveUpdateResponse toStoreSaveUpdateResponse(Store store) {
        return new StoreSaveUpdateResponse(store.getStoreId(), store.getName(), store.getAddress(), store.getPhone());
    }
}
