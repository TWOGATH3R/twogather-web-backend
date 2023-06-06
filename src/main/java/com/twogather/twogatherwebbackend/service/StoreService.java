package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.StoreType;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveListRequest;
import com.twogather.twogatherwebbackend.dto.store.*;
import com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.ImageRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.MenuRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException.AccessDeniedExceptionErrorCode.ACCESS_DENIED;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_USERNAME;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.INVALID_STORE_TYPE;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

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
    private final CategoryService categoryService;

    public StoreSaveUpdateResponse save(final Long categoryId,
                                        final StoreSaveUpdateRequest storeRequest,
                                        final MenuSaveListRequest menuRequest,
                                        final List<MultipartFile> storeImageList,
                                        final List<String> keywordList){
        String username = SecurityUtils.getUsername();
        StoreOwner owner = storeOwnerRepository.findByUsername(username).orElseThrow(
                ()->new MemberException(NO_SUCH_USERNAME)
        );
        validateDuplicateName(storeRequest.getStoreName());
        Store store = new Store(owner, storeRequest.getStoreName(), storeRequest.getAddress(), storeRequest.getPhone());
        Store savedStore = storeRepository.save(store);

        storeKeywordService.setStoreKeyword(store.getStoreId(), keywordList);
        menuService.saveList(store.getStoreId(), menuRequest.getMenuSaveList());
        imageService.upload(store.getStoreId(), storeImageList);
        categoryService.setCategoriesForStore(store.getStoreId(), categoryId);

        return toStoreSaveUpdateResponse(savedStore);
    }
    public boolean isMyStore(Long storeId) {
        String username = SecurityUtils.getUsername();
        Member member = memberRepository.findByUsername(username).orElseThrow(
                ()-> new MemberException(NO_SUCH_USERNAME)
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
        return storeRepository.findTopNByType(n, type.name(), "desc");
    }

    public void delete(Long storeId) {
        Store store =  storeRepository.findById(storeId).orElseThrow(() -> new StoreException(NO_SUCH_STORE));
        storeRepository.delete(store);
    }

    public StoreSaveUpdateResponse update(final Long storeId, final StoreSaveUpdateRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreException(NO_SUCH_STORE));
        if (request.getStoreName() != null && !request.getStoreName().isEmpty() && !request.getStoreName().equals(store.getName())) {
            validateDuplicateName(request.getStoreName());
            store.updateName(request.getStoreName());
        }
        store.updateAddress(request.getAddress());
        store.updatePhone(request.getPhone());

        return toStoreSaveUpdateResponse(store);
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
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreException(NO_SUCH_STORE));
        return toStoreSaveUpdateResponse(store);
    }

    private void validateDuplicateName(String name){
        if (storeRepository.existsByName(name)) {
            throw new StoreException(StoreException.StoreErrorCode.DUPLICATE_NAME);
        }
    }
    private StoreResponse toStoreResponse(Store store){
        //TODO: 구현
        return null;
    }
    private StoreSaveUpdateResponse toStoreSaveUpdateResponse(Store store) {
        return new StoreSaveUpdateResponse(store.getStoreId(), store.getName(), store.getAddress(), store.getPhone());
    }
}
