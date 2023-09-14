package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.store.StoreSearchType;
import com.twogather.twogatherwebbackend.dto.store.*;
import com.twogather.twogatherwebbackend.exception.CategoryException;
import com.twogather.twogatherwebbackend.exception.KeywordException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.CategoryRepository;
import com.twogather.twogatherwebbackend.repository.LikeRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.CacheNames;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.exception.CategoryException.CategoryErrorCode.NO_SUCH_CATEGORY;
import static com.twogather.twogatherwebbackend.exception.KeywordException.KeywordErrorCode.MAXIMUM_KEYWORD_LIMIT;
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
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final LikeRepository likeRepository;

    @Value("${keyword.max.size}")
    private Integer keywordMaxSize;

    public void approveStore(final Long storeId){
        Store store = storeRepository.findById(storeId).orElseThrow(
                ()->new StoreException(NO_SUCH_STORE)
        );
        store.approve();
    }
    public void reapply(final Long storeId){
        Store store = storeRepository.findById(storeId).orElseThrow(
                ()->new StoreException(NO_SUCH_STORE)
        );
        store.reapply();
    }
    public void rejectStore(final Long storeId, final RejectReason rejectReason){
        Store store = storeRepository.findById(storeId).orElseThrow(
                ()->new StoreException(NO_SUCH_STORE)
        );
        store.reject(rejectReason.getReason());
    }
    public void setAllStoreDetail(){
        //모든 store의 like수, 리뷰 수, 리뷰 평점을 계산해서 집어 넣는다
        List<Store> storeList = storeRepository.findAll();
        List<Long> storeIdList = storeList.stream()
                .map(Store::getStoreId)
                .collect(Collectors.toList());

        Map<Long, Long> likesCountMap = likeRepository.countLikesByStoreIds(storeIdList);
        Map<Long, Long> reviewsCountMap = reviewRepository.countReviewsByStoreIds(storeIdList);
        Map<Long, Double> averageReviewScoreMap = reviewRepository.averageScoresByStoreIds(storeIdList);

        for (Store store : storeList) {
            Long storeId = store.getStoreId();
            Long likeCount = likesCountMap.getOrDefault(storeId, 0L);
            Long reviewCount = reviewsCountMap.getOrDefault(storeId, 0L);
            Double avgReviewScore = averageReviewScoreMap.getOrDefault(storeId, 0.0);

            store.setDetail(likeCount, reviewCount, avgReviewScore);
        }
    }
    public StoreSaveUpdateResponse getStoreDetail(final Long storeId){
        return storeRepository.findStoreDetail(storeId).orElseThrow(()->new StoreException(NO_SUCH_STORE));
    }
    public StoreSaveUpdateResponse save(final StoreSaveUpdateRequest storeRequest){
        validateDuplicateName(storeRequest.getStoreName());
        validateKeywordList(storeRequest.getKeywordIdList());
        //validationBizRegNumber(storeRequest); TODO: 나중에 추가
        String username = SecurityUtils.getLoginUsername();
        StoreOwner owner = storeOwnerRepository.findByUsername(username).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        Store store = new Store(owner, storeRequest.getStoreName(), storeRequest.getAddress(), storeRequest.getPhone(),
                storeRequest.getBusinessName(), storeRequest.getBusinessNumber(), storeRequest.getBusinessStartDate());
        Store savedStore = storeRepository.save(store);

        List<String> keywordNameList = storeKeywordService.setStoreKeyword(store.getStoreId(), storeRequest.getKeywordIdList());
        Category category = categoryRepository.findById(storeRequest.getCategoryId()).orElseThrow(()-> new CategoryException(NO_SUCH_CATEGORY));
        store.setCategory(category);


        return StoreSaveUpdateResponse.builder()
                .address(savedStore.getAddress())
                .storeId(savedStore.getStoreId())
                .businessName(savedStore.getBusinessName())
                .businessNumber(savedStore.getBusinessNumber())
                .categoryName(category.getName())
                .keywordList(keywordNameList)
                .phone(savedStore.getPhone())
                .businessStartDate(savedStore.getBusinessStartDate())
                .storeName(savedStore.getName()).build();

    }
    public Page<MyLikeStoreResponse> findMyLikeStore(Long memberId, Pageable pageable){
        return storeRepository.findMyLikeStore(memberId, pageable);
    }
    public boolean isMyStore(Long storeId) {
        String username = SecurityUtils.getLoginUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                () -> new MemberException(NO_SUCH_MEMBER)
        );
        Store store = storeRepository.findById(storeId).orElseThrow(()->
                new StoreException(NO_SUCH_STORE)
        );
        if (!store.getOwner().getMemberId().equals(member.getMemberId())) {
            return false;
        }
        return true;
    }
    @Cacheable(cacheNames = CacheNames.TOP_STORE, key="#type.name().concat('-').concat(#n)")
    public List<TopStoreResponse> getStoresTopN(StoreSearchType type, int n){
        return storeRepository.findTopNByType(n, type.name(), "desc");
    }

    public void delete(Long storeId) {
        storeRepository.deleteById(storeId);
    }

    public StoreSaveUpdateResponse update(final Long storeId, final StoreSaveUpdateRequest request) {
        validateKeywordList(request.getKeywordIdList());
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreException(NO_SUCH_STORE));
        //TODO: biz 유효성 검사 필요
        validateDuplicateName(store.getName(), request.getStoreName());
        List<String> keywordNameList = storeKeywordService.setStoreKeyword(store.getStoreId(), request.getKeywordIdList());
        store.update(request.getStoreName(), request.getAddress(), request.getPhone(), request.getBusinessName(), request.getBusinessNumber(), request.getBusinessStartDate());
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()->new CategoryException(NO_SUCH_CATEGORY));

        return StoreSaveUpdateResponse.builder()
                .address(store.getAddress())
                .storeId(store.getStoreId())
                .businessName(store.getBusinessName())
                .businessNumber(store.getBusinessNumber())
                .categoryName(category.getName())
                .keywordList(keywordNameList)
                .phone(store.getPhone())
                .businessStartDate(store.getBusinessStartDate())
                .storeName(store.getName()).build();
    }
    public Page<StoreResponseWithKeyword> getStores(
            Pageable pageable, String categoryName, String keyword,String location, String storeName){
        return storeRepository.findStoresByCondition(pageable, categoryName, keyword, location, storeName);
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
    private void validateDuplicateName(String storeName, String requestStoreName){
        if(!storeName.equals(requestStoreName) && storeRepository.existsByName(requestStoreName)){
             throw new StoreException(DUPLICATE_NAME);
        }
    }
    private void validationBizRegNumber(final StoreSaveUpdateRequest request){
        boolean isValid = validator.validateBizRegNumber(request.getBusinessNumber(), request.getBusinessStartDate(), request.getBusinessName());
        if(!isValid){
            throw new StoreException(BIZ_REG_NUMBER_VALIDATION);
        }
    }
    public void validateKeywordList(final List<Long> keywordList){
        if(keywordList.size()>keywordMaxSize) throw new KeywordException(MAXIMUM_KEYWORD_LIMIT);
    }
}
