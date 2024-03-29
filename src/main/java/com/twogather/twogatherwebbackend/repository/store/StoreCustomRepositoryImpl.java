package com.twogather.twogatherwebbackend.repository.store;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.common.FixedPageRequest;
import com.twogather.twogatherwebbackend.dto.store.*;
import com.twogather.twogatherwebbackend.exception.SQLException;
import com.twogather.twogatherwebbackend.repository.StoreKeywordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.domain.QCategory.category;
import static com.twogather.twogatherwebbackend.domain.QImage.image;
import static com.twogather.twogatherwebbackend.domain.QKeyword.keyword;
import static com.twogather.twogatherwebbackend.domain.QMember.member;
import static com.twogather.twogatherwebbackend.domain.QStoreOwner.storeOwner;
import static com.twogather.twogatherwebbackend.domain.QLikes.likes;
import static com.twogather.twogatherwebbackend.domain.QReview.review;
import static com.twogather.twogatherwebbackend.domain.QStore.store;
import static com.twogather.twogatherwebbackend.domain.QStoreKeyword.storeKeyword;
import static com.twogather.twogatherwebbackend.exception.SQLException.SQLErrorCode.INVALID_REQUEST_PARAM;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class StoreCustomRepositoryImpl implements StoreCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;
    private final StoreKeywordRepository storeKeywordRepository;

    public StoreCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory,
                                     StoreKeywordRepository storeKeywordRepository) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.storeKeywordRepository = storeKeywordRepository;
    }

    @Override
    public Optional<StoreDefaultResponse> findDefaultActiveStoreInfo(Long storeId){
        StoreDefaultResponse results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        StoreDefaultResponse.class,
                                        storeOwner.memberId,
                                        store.storeId,
                                        store.name,
                                        store.address,
                                        store.phone,
                                        store.category.name,
                                        store.likesList.size(),
                                        MathExpressions.round(review.score.avg(), 1)
                                ))
                        .from(store)
                        .leftJoin(store.reviewList, review)
                        .leftJoin(store.category, category)
                        .leftJoin(store.likesList, likes)
                        .leftJoin(store.owner, storeOwner)
                        .where(store.status.eq(StoreStatus.APPROVED))
                        .where(store.storeId.eq(storeId))
                        .groupBy(store.storeId)
                        .fetchOne();
        List<Keyword> keywordList = storeKeywordRepository.findKeywordsByStoreId(storeId);

        List<String> keywordNames = keywordList.stream()
                .limit(3)
                .map(Keyword::getName)
                .collect(Collectors.toList());
        results.setKeywordList(keywordNames);

        return Optional.of(results);
    }

    @Override
    public Optional<StoreSaveUpdateResponse> findStoreDetail(Long storeId) {
        Store storeQuery = jpaQueryFactory
                .selectFrom(store)
                .leftJoin(store.storeKeywordList, storeKeyword)
                .leftJoin(storeKeyword.keyword, keyword)
                .leftJoin(store.category, category)
                .where(store.storeId.eq(storeId))
                .groupBy(store.storeId)
                .fetchOne();

        StoreSaveUpdateResponse storeResponse = createStoreDetailResponse(storeQuery);

        return Optional.of(storeResponse);
    }

    @Override
    public List<TopStoreResponse> findTopNByType(int n, String order, String orderBy) {

        List<TopStoreResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        TopStoreResponse.class,
                                        store.storeId,
                                        store.name,
                                        store.avgReviewRating,
                                        store.address,
                                        image.url,
                                        store.likeCount
                                ))
                        .from(store)
                        .leftJoin(store.storeImageList, image)
                        .where(store.status.eq(StoreStatus.APPROVED))
                        .groupBy(store.storeId)
                        .orderBy(createOrderSpecifiersWithTopN(order, orderBy))
                        .limit(n)
                        .fetch();

        return results;
    }

    @Override
    public Page<MyStoreResponse> findStoresByStatus(StoreStatus status, Pageable pageable){
        List<Store> storeQuery = jpaQueryFactory
                .selectFrom(store)
                .where(store.status.eq(status))
                .leftJoin(store.storeImageList, image)
                .groupBy(store.storeId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int count = jpaQueryFactory
                .selectFrom(store)
                .where(store.status.eq(status))
                .groupBy(store.storeId)
                .fetch().size();


        List<MyStoreResponse> storeResponses = createMyStoreResponse(storeQuery);

        return new PageImpl<>(storeResponses, pageable, count);
    }

    @Override
    public Page<MyStoreResponse> findMyStore(Long ownerId, Pageable pageable) {
        List<Store> storeQuery = jpaQueryFactory
                .selectFrom(store)
                .where(store.owner.memberId.eq(ownerId))
                .leftJoin(store.owner, storeOwner)
                .leftJoin(store.storeImageList, image)
                .groupBy(store.storeId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int count = jpaQueryFactory
                .selectFrom(store)
                .where(store.owner.memberId.eq(ownerId))
                .groupBy(store.storeId)
                .fetch()
                .size();

        List<MyStoreResponse> storeResponses = createMyStoreResponse(storeQuery);

        return new PageImpl<>(storeResponses, pageable, count);
    }

    @Override
    public Page<MyLikeStoreResponse> findMyLikeStore(Long memberId, Pageable pageable) {
        List<Store> storeQuery =
                jpaQueryFactory
                        .select(likes.store)
                        .from(likes)
                        .join(likes.store, store)
                        .join(likes.member, member)
                        .leftJoin(store.storeImageList, image).fetchJoin()
                        .leftJoin(store.storeKeywordList, storeKeyword)
                        .leftJoin(storeKeyword.keyword, keyword)
                        .where(member.memberId.eq(memberId))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        List<MyLikeStoreResponse> response = createMyLikeStoreResponse(storeQuery);

        int count = jpaQueryFactory
                .select(store.storeId)
                .from(likes)
                .join(likes.store, store)
                .join(likes.member, member)
                .where(member.memberId.eq(memberId))
                .fetch()
                .size();
        return new PageImpl<>(response, pageable, count);
    }

    @Override
    public Page<StoreResponseWithKeyword> findStoresByCondition(Pageable pageable,
                                                                String category,
                                                                String keyword,
                                                                String location,
                                                                String storeName,
                                                                Boolean useSearchBtn) {
        List<StoreResponseWithKeyword> storeQuery = null;
        JPAQuery<Long> count;
        if(!keyword.isBlank()){
            storeQuery = jpaQueryFactory
                    .select(
                            Projections.constructor(
                                    StoreResponseWithKeyword.class,
                                    store.storeId,
                                    store.name,
                                    store.address,
                                    store.avgReviewRating,
                                    //keywordlist
                                    //image.url,
                                    store.likeCount
                            ))
                    .from(store)
                    .where(store.status.eq(StoreStatus.APPROVED))
                    .where(
                            categoryEq(category),
                            keywordContain(keyword),
                            addressContain(location),
                            storeNameContain(storeName)
                    )
                    .innerJoin(store.category, QCategory.category)
                    .leftJoin(store.storeKeywordList, storeKeyword)
                    .leftJoin(storeKeyword.keyword, QKeyword.keyword)
                    .orderBy(createOrderSpecifiers(pageable))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            count = jpaQueryFactory
                    .select(store.count())
                    .from(storeKeyword)
                    .where(store.status.eq(StoreStatus.APPROVED))
                    .where(
                            categoryEq(category),
                            keywordContain(keyword),
                            addressContain(location),
                            storeNameContain(storeName)
                    )
                    .innerJoin(storeKeyword.store, store)
                    .innerJoin(storeKeyword.keyword, QKeyword.keyword);
        }else{
            storeQuery = jpaQueryFactory
                    .select(
                            Projections.constructor(
                                    StoreResponseWithKeyword.class,
                                    store.storeId,
                                    store.name,
                                    store.address,
                                    store.avgReviewRating,
                                    //keywordlist
                                    //image.url,
                                    store.likeCount
                            ))
                    .from(store)
                    .where(store.status.eq(StoreStatus.APPROVED))
                    .where(
                            categoryEq(category),
                            addressContain(location),
                            storeNameContain(storeName)
                    )
                    .innerJoin(store.category, QCategory.category)
                    .orderBy(createOrderSpecifiers(pageable))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            count = jpaQueryFactory
                    .select(store.count())
                    .from(store)
                    .where(store.status.eq(StoreStatus.APPROVED))
                    .where(
                            categoryEq(category),
                            keywordContain(keyword),
                            addressContain(location),
                            storeNameContain(storeName)
                    );
        }

        List<StoreResponseWithKeyword> list = new ArrayList<>();
        List<Long> storeIds = storeQuery.stream().map(StoreResponseWithKeyword::getStoreId).collect(Collectors.toList());

        // Image URL 및 Keyword 한 번에 가져오기
        List<Tuple> combinedData = jpaQueryFactory
                .select(store.storeId, image.url, QKeyword.keyword.name)
                .from(store)
                .leftJoin(store.storeImageList, image)
                .leftJoin(store.storeKeywordList, storeKeyword)
                .innerJoin(storeKeyword.keyword, QKeyword.keyword)
                .where(store.storeId.in(storeIds))
                .fetch();

        Map<Long, String> storeIdToImageUrl = new HashMap<>(storeIds.size());
        Map<Long, Set<String>> storeIdToKeywordNames = new HashMap<>(storeIds.size());

        for (Tuple tuple : combinedData) {
            Long storeId = tuple.get(store.storeId);
            String imageUrl = tuple.get(image.url);
            String keywordName = tuple.get(QKeyword.keyword.name);

            storeIdToImageUrl.putIfAbsent(storeId, imageUrl);

            if (keywordName != null) {
                storeIdToKeywordNames.computeIfAbsent(storeId, k -> new HashSet<>()).add(keywordName);
            }
        }

        for (StoreResponseWithKeyword response : storeQuery) {
            String storedImageUrl = storeIdToImageUrl.getOrDefault(response.getStoreId(), "");
            response.setStoreImageUrl(storedImageUrl);

            Set<String> keywordList = storeIdToKeywordNames.getOrDefault(response.getStoreId(), Collections.emptySet());
            response.setKeywordList(new ArrayList<>(keywordList));
            list.add(response);
        }

        if(useSearchBtn) {
            int fixedPageCount = 10 * pageable.getPageSize();
            return new PageImpl<>(list, pageable, fixedPageCount);
        }
        long totalCount = Optional.ofNullable(count.fetchOne()).orElse(0L);
        Pageable pageRequest = new FixedPageRequest(pageable, totalCount);
        return new PageImpl<>(list, pageRequest, totalCount);
    }
    private OrderSpecifier<?> createOrderSpecifiersWithTopN(String order, String orderBy) {
        Order direction = orderBy.equals("asc") ? Order.ASC : Order.DESC;

        switch (order){
            case "MOST_REVIEWED":
                return new OrderSpecifier(direction, store.reviewCount);
            case "TOP_RATED":
                return new OrderSpecifier(direction, store.avgReviewRating);
            case "MOST_LIKES_COUNT":
                return new OrderSpecifier(direction, store.likeCount);
            default:
                throw new SQLException(INVALID_REQUEST_PARAM);
        }

    }
    private OrderSpecifier<?> createOrderSpecifiers(Pageable page) {
        if(!page.getSort().isEmpty()){
            for (Sort.Order order : page.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()){
                    case "MOST_REVIEWED":
                        return new OrderSpecifier(direction, store.reviewCount);
                    case "TOP_RATED":
                        return new OrderSpecifier(direction, store.avgReviewRating);
                    case "MOST_LIKES_COUNT":
                        return new OrderSpecifier(direction, store.likeCount);
                    default:
                        throw new SQLException(INVALID_REQUEST_PARAM);
                }
            }
        }
        return new OrderSpecifier<>(Order.DESC, store.storeId);

    }
    private StoreSaveUpdateResponse createStoreDetailResponse(Store store){
        List<String> keywordNameList = store.getStoreKeywordList().stream()
                .limit(3)
                .map(s->s.getKeyword().getName())
                .collect(Collectors.toList());
        String categoryName = store.getCategory().getName();

        return StoreSaveUpdateResponse
                .builder()
                .storeId(store.getStoreId())
                .keywordList(keywordNameList)
                .storeName(store.getName())
                .businessStartDate(store.getBusinessStartDate())
                .businessNumber(store.getBusinessNumber())
                .businessName(store.getBusinessName())
                .phone(store.getPhone())
                .address(store.getAddress())
                .categoryName(categoryName).build();
    }
    private Double roundToTwoDecimal(Double score){
        return Math.round(score * 10.0) / 10.0;
    }
    private BooleanExpression keywordContain(String keyword) {
        if (!StringUtils.hasText(keyword) || storeKeyword==null) {
            return Expressions.asBoolean(true).isTrue();
        } else {
            return storeKeyword.keyword.name.eq(keyword);
        }
    }
    private BooleanExpression storeNameContain(String storeName){
        if (!StringUtils.hasText(storeName)) {
            return Expressions.asBoolean(true).isTrue();
        } else {
            return store.name.contains(storeName);
        }
    }
    private BooleanExpression categoryEq(String category) {
        if (!StringUtils.hasText(category)) {
            return Expressions.asBoolean(true).isTrue();
        } else {
            return store.category.name.eq(category);
        }
    }
    private BooleanExpression addressContain(String address) {
        if (!StringUtils.hasText(address)) {
            return Expressions.asBoolean(true).isTrue();
        } else {
            return store.address.like(address+ "%");
        }
    }
    private String getUrl(List<Image> imageList){
        if(imageList.isEmpty()) return "";
        return imageList.get(0).getUrl();
    }
    private List<MyStoreResponse> createMyStoreResponse(List<Store> storeQuery){
        return storeQuery
                .stream()
                .map(store ->
                        MyStoreResponse.builder()
                                .storeImageUrl(getUrl(store.getStoreImageList()))
                                .status(store.getStatus())
                                .phone(store.getPhone())
                                .reasonForRejection(store.getReasonForRejection())
                                .requestDate(store.getRequestDate())
                                .storeId(store.getStoreId())
                                .address(store.getAddress())
                                .storeName(store.getName())
                                .build()).collect(Collectors.toList());
    }
    private List<MyLikeStoreResponse> createMyLikeStoreResponse(List<Store> storeQuery){
        return storeQuery
                .stream()
                .map(store ->
                        MyLikeStoreResponse
                                .builder()
                                .storeImageUrl(getUrl(store.getStoreImageList()))
                                .phone(store.getPhone())
                                .storeId(store.getStoreId())
                                .address(store.getAddress())
                                .storeName(store.getName())
                                .keywordList(store.getStoreKeywordList().stream()
                                        .limit(3)
                                        .map(storeKeyword -> storeKeyword.getKeyword().getName())
                                        .collect(Collectors.toList()))
                                .build()
                ).collect(Collectors.toList());
    }
}