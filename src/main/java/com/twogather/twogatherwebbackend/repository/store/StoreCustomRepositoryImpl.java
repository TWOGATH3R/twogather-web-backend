package com.twogather.twogatherwebbackend.repository.store;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.store.MyStoreResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreResponseWithKeyword;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import com.twogather.twogatherwebbackend.exception.SQLException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.domain.QImage.image;
import static com.twogather.twogatherwebbackend.domain.QLikes.likes;
import static com.twogather.twogatherwebbackend.domain.QReview.review;
import static com.twogather.twogatherwebbackend.domain.QStore.store;
import static com.twogather.twogatherwebbackend.domain.QStoreKeyword.storeKeyword;
import static com.twogather.twogatherwebbackend.exception.SQLException.SQLErrorCode.INVALID_REQUEST_PARAM;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class StoreCustomRepositoryImpl implements StoreCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public StoreCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
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
                                MathExpressions.round(review.score.avg(), 1),
                                store.address,
                                image.url
                        ))
                .from(store)
                        .leftJoin(store.likesList, likes)
                        .leftJoin(store.reviewList, review)
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

        List<MyStoreResponse> storeResponses = storeQuery
                .stream()
                .map(store ->
                        MyStoreResponse.builder()
                                .storeImageUrl(getUrl(store.getStoreImageList()))
                                .isApproved(store.getStatus().equals(StoreStatus.APPROVED))
                                .phone(store.getPhone())
                                .reasonForRejection(store.getReasonForRejection())
                                .requestDate(store.getRequestDate())
                                .storeId(store.getStoreId())
                                .address(store.getAddress())
                                .name(store.getName())
                                .build()).collect(Collectors.toList());

        return new PageImpl<>(storeResponses, pageable, storeResponses.size());
    }

    public Page<StoreResponseWithKeyword> findStoresByCondition(Pageable pageable, String category, String keyword, String location) {
        List<Store> storeQuery = jpaQueryFactory
                .selectFrom(store)
                .where(store.status.eq(StoreStatus.APPROVED))
                .where(
                        categoryEq(category),
                        keywordContain(keyword),
                        addressContain(location)
                )
                .leftJoin(store.reviewList, review)
                .leftJoin(store.storeImageList, image)
                .leftJoin(store.likesList, likes)
                .leftJoin(store.category, QCategory.category)
                .leftJoin(store.storeKeywordList, storeKeyword)
                .orderBy(createOrderSpecifiers(pageable))
                .groupBy(store.storeId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<StoreResponseWithKeyword> storeResponses = storeQuery.stream().map(store -> {
                    Long storeId = store.getStoreId();
                    String name = store.getName();
                    String address = store.getAddress();
                    List<String> keywordList = store.getStoreKeywordList().stream()
                            .map(storeKeyword -> storeKeyword.getKeyword().getName())
                            .collect(Collectors.toList());

                    Double score = store.getReviewList().stream()
                            .mapToDouble(Review::getScore)
                            .average()
                            .orElse(0.0);

                    score = roundToTwoDecimal(score);

                    String storeImageUrl = store.getStoreImageList().isEmpty() || store.getStoreImageList().get(0).getUrl() == null
                            ? ""
                            : store.getStoreImageList().get(0).getUrl();

                    return new StoreResponseWithKeyword(storeId, name, address, score, keywordList, storeImageUrl);
                })
                .collect(Collectors.toList());


        return new PageImpl<>(storeResponses, pageable, storeResponses.size());
    }
    private OrderSpecifier<?> createOrderSpecifiersWithTopN(String order, String orderBy) {
        Order direction = orderBy.equals("asc") ? Order.ASC : Order.DESC;

        switch (order){
            case "MOST_REVIEWED":
                return new OrderSpecifier(direction, store.reviewList.size());
            case "TOP_RATED":
                return new OrderSpecifier(direction, review.score.avg());
            case "MOST_LIKES_COUNT":
                return new OrderSpecifier(direction, store.likesList.size());
            default:
                throw new SQLException(INVALID_REQUEST_PARAM);
        }

    }
    private OrderSpecifier<?> createOrderSpecifiers(Pageable page) {
        if(!page.getSort().isEmpty()){
            for (Sort.Order order : page.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()){
                    case "reviewsCount":
                        return new OrderSpecifier(direction, store.reviewList.size());
                    case "avgScore":
                        return new OrderSpecifier(direction, review.score.avg());
                    case "likesCount":
                        return new OrderSpecifier(direction, store.likesList.size());
                    default:
                        throw new SQLException(INVALID_REQUEST_PARAM);
                }
            }
        }
        return null;

    }
    private Double roundToTwoDecimal(Double score){
        return Math.round(score * 10.0) / 10.0;
    }
    private BooleanExpression keywordContain(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Expressions.asBoolean(true).isTrue();
        } else {
            return store.storeKeywordList.any().keyword.name.eq(keyword);
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
            return store.address.contains(address);
        }
    }
    private String getUrl(List<Image> imageList){
        if(imageList.isEmpty()) return "";
        return imageList.get(0).getUrl();
    }

}