package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveListRequest;
import com.twogather.twogatherwebbackend.dto.StoreSearchType;
import com.twogather.twogatherwebbackend.dto.store.*;
import com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException;
import com.twogather.twogatherwebbackend.exception.CustomAuthenticationException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.ImageRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.MenuRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import javax.validation.Valid;
import java.util.List;

import static com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException.AccessDeniedExceptionErrorCode.ACCESS_DENIED;
import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.UNAUTHORIZED;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_MEMBER;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final MenuService menuService;
    private final ImageService imageService;
    private final StoreKeywordService storeKeywordService;
    private final StoreOwnerRepository storeOwnerRepository;
    private final BizRegNumberValidator validator;
    private final CategoryService categoryService;
    private final BusinessHourService businessHourService;

    //TODO: isApproved, reasonForRejection 추가되었으니 아래 메서드 다시 작성

    public void approveStore(final Long storeId){
        Store store = storeRepository.findAllStoreById(storeId).orElseThrow(
                ()->new StoreException(NO_SUCH_STORE)
        );
        store.approve();
    }
    public StoreSaveUpdateResponse save(final Long categoryId,
                                        final BusinessHourSaveUpdateListRequest businessRequest,
                                        final StoreSaveUpdateRequest storeRequest,
                                        final MenuSaveListRequest menuRequest,
                                        final List<MultipartFile> storeImageList,
                                        final List<String> keywordList){
        validationBizRegNumber(storeRequest);
        String username = SecurityUtils.getUsername();
        StoreOwner owner = storeOwnerRepository.findByUsername(username).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        validateDuplicateName(storeRequest.getStoreName());
        Store store = new Store(owner, storeRequest.getStoreName(), storeRequest.getAddress(), storeRequest.getPhone(),
                storeRequest.getBusinessName(), storeRequest.getBusinessNumber(), storeRequest.getBusinessStartDate());
        Store savedStore = storeRepository.save(store);

        businessHourService.saveList(store.getStoreId(), businessRequest.getBusinessHourList());
        storeKeywordService.setStoreKeyword(store.getStoreId(), keywordList);
        menuService.saveList(store.getStoreId(), menuRequest.getMenuSaveList());
        imageService.upload(store.getStoreId(), storeImageList);
        categoryService.setCategoriesForStore(store.getStoreId(), categoryId);

        return StoreSaveUpdateResponse.from(savedStore.getStoreId(), savedStore.getName(), savedStore.getAddress(), savedStore.getPhone());

    }
    public boolean isMyStore(Long storeId) {
        String username = SecurityUtils.getUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                () ->new CustomAccessDeniedException(ACCESS_DENIED)
        );
        Store store = storeRepository.findAllStoreById(storeId).orElseThrow(()->
                new CustomAccessDeniedException(ACCESS_DENIED)
        );
        if (!store.getOwner().getMemberId().equals(member.getMemberId())) {
            throw new CustomAccessDeniedException(ACCESS_DENIED);
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


        return StoreSaveUpdateResponse.from(store.getStoreId(), store.getName(), store.getAddress(), store.getPhone());

    }
    public Page<StoreResponseWithKeyword> getStores(
            Pageable pageable, String categoryName, String keyword,String location){
        return storeRepository.findStoresByCondition(pageable, categoryName, keyword, location);
    }
    public Page<MyStoreResponse> getStoresByOwner(Long storeOwnerId, Pageable pageable){
        //TODO: 구현
        return null;
    }
    public StoreSaveUpdateResponse getStore(Long storeId){
        Store store = storeRepository.findActiveStoreById(storeId).orElseThrow(() -> new StoreException(NO_SUCH_STORE));

        return StoreSaveUpdateResponse.from(store.getStoreId(), store.getName(), store.getAddress(), store.getPhone());
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
