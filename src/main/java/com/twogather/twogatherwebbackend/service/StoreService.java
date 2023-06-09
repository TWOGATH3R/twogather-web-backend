package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.dto.StoreSearchType;
import com.twogather.twogatherwebbackend.dto.store.*;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_MEMBER;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final StoreKeywordService storeKeywordService;
    private final StoreOwnerRepository storeOwnerRepository;
    private final BizRegNumberValidator validator;
    private final CategoryService categoryService;
    private final KeywordService keywordService;

    public void approveStore(final Long storeId){
        Store store = storeRepository.findAllStoreById(storeId).orElseThrow(
                ()->new StoreException(NO_SUCH_STORE)
        );
        store.approve();
    }
    public void reapply(final Long storeId){
        Store store = storeRepository.findAllStoreById(storeId).orElseThrow(
                ()->new StoreException(NO_SUCH_STORE)
        );
        store.reapply();
    }
    public void rejectStore(final Long storeId, final RejectReason rejectReason){
        Store store = storeRepository.findAllStoreById(storeId).orElseThrow(
                ()->new StoreException(NO_SUCH_STORE)
        );
        store.reject(rejectReason.getReason());
    }
    public StoreSaveUpdateResponse save(final StoreSaveUpdateRequest storeRequest){
        validationBizRegNumber(storeRequest);
        String username = SecurityUtils.getLoginUsername();
        StoreOwner owner = storeOwnerRepository.findByUsername(username).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        validateDuplicateName(storeRequest.getStoreName());
        Store store = new Store(owner, storeRequest.getStoreName(), storeRequest.getAddress(), storeRequest.getPhone(),
                storeRequest.getBusinessName(), storeRequest.getBusinessNumber(), storeRequest.getBusinessStartDate());
        Store savedStore = storeRepository.save(store);

        List<String> keywordNameList = storeKeywordService.setStoreKeyword(store.getStoreId(), storeRequest.getKeywordIdList());
        categoryService.setCategoriesForStore(store.getStoreId(), storeRequest.getCategoryId());

        return StoreSaveUpdateResponse.builder()
                .address(savedStore.getAddress())
                .storeId(savedStore.getStoreId())
                .businessName(savedStore.getBusinessName())
                .businessNumber(savedStore.getBusinessNumber())
                .categoryId(storeRequest.getCategoryId())
                .keywordList(keywordNameList)
                .phone(savedStore.getPhone())
                .businessStartDate(savedStore.getBusinessStartDate())
                .storeName(savedStore.getName()).build();

    }
    public boolean isMyStore(Long storeId) {
        String username = SecurityUtils.getLoginUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                () -> new MemberException(NO_SUCH_MEMBER)
        );
        Store store = storeRepository.findAllStoreById(storeId).orElseThrow(()->
                new StoreException(NO_SUCH_STORE)
        );
        if (!store.getOwner().getMemberId().equals(member.getMemberId())) {
            throw new StoreException(NO_SUCH_STORE);
        }
        return true;
    }
    public List<TopStoreResponse> getStoresTopN(StoreSearchType type, int n){
        return storeRepository.findTopNByType(n, type.name(), "desc");
    }

    public void delete(Long storeId) {
        Store store =  storeRepository.findById(storeId).orElseThrow(() -> new StoreException(NO_SUCH_STORE));
        store.delete();;
    }

    public StoreSaveUpdateResponse update(final Long storeId, final StoreSaveUpdateRequest request) {
        Store store = storeRepository.findActiveStoreById(storeId).orElseThrow(() -> new StoreException(NO_SUCH_STORE));
        store.update(request.getStoreName(), request.getAddress(), request.getPhone(), request.getBusinessName(), request.getBusinessNumber(), request.getBusinessStartDate());


        return StoreSaveUpdateResponse.builder()
                .address(store.getAddress())
                .storeId(store.getStoreId())
                .businessName(store.getBusinessName())
                .businessNumber(store.getBusinessNumber())
                .categoryId(request.getCategoryId())
                .keywordList(keywordService.getKeywordNameList(request.getKeywordIdList()))
                .phone(store.getPhone())
                .businessStartDate(store.getBusinessStartDate())
                .storeName(store.getName()).build();
    }
    public Page<StoreResponseWithKeyword> getStores(
            Pageable pageable, String categoryName, String keyword,String location){
        return storeRepository.findStoresByCondition(pageable, categoryName, keyword, location);
    }
    public Page<MyStoreResponse> getStores(StoreStatus type, Pageable pageable){
        return storeRepository.findStoresByStatus(type, pageable);
    }
    public Page<MyStoreResponse> getStoresByOwner(Long ownerId, Pageable pageable){
        return storeRepository.findMyStore(ownerId, pageable);
    }
    public StoreDefaultResponse getStore(Long storeId){
        return storeRepository.findDefaultActiveStoreInfo(storeId).orElseThrow(() -> new StoreException(NO_SUCH_STORE));
    }

    private void validateDuplicateName(String name){
        if (storeRepository.existsByName(name)) {
            throw new StoreException(StoreException.StoreErrorCode.DUPLICATE_NAME);
        }
    }
    private void validationBizRegNumber(final StoreSaveUpdateRequest request){
        boolean isValid = validator.validateBizRegNumber(request.getBusinessNumber(), request.getBusinessStartDate(), request.getBusinessName());
        if(!isValid){
            throw new StoreException(BIZ_REG_NUMBER_VALIDATION);
        }
    }
}
