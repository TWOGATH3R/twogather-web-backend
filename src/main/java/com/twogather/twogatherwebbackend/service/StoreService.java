package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import com.twogather.twogatherwebbackend.dto.store.*;
import com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException.AccessDeniedExceptionErrorCode.ACCESS_DENIED;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_EMAIL;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.STORE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    //TODO: isApproved, reasonForRejection 추가되었으니 아래 메서드 다시 작성

    public StoreResponse save(final StoreSaveRequest request){
        validateDuplicateName(request.getName());
        Store store = new Store(request.getName(), request.getAddress(), request.getPhone());
        Store savedStore = storeRepository.save(store);
        return toStoreResponse(savedStore);
    }
    public boolean isMyStore(Long storeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Member member = memberRepository.findByEmail(currentUsername).orElseThrow(
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
    public TopStoreInfoPreviewResponse getStoresTop10Preview(){
        //TODO: 구현

        return null;
    }
    public List<TopStoreInfoResponse> getStoresTop10(String type){
        //TODO: 구현

        return null;
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
    public List<StoresResponse> getStores(
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
        return storeRepository.findById(storeId).orElseThrow(() -> new StoreException(STORE_NOT_FOUND));
    }
    private StoreResponse toStoreResponse(Store store) {
        return new StoreResponse(store.getStoreId(), store.getName(), store.getAddress(), store.getPhone());
    }
}
