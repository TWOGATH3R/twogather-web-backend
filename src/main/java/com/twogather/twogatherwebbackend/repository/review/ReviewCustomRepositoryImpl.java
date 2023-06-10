package com.twogather.twogatherwebbackend.repository.review;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twogather.twogatherwebbackend.domain.QImage;
import com.twogather.twogatherwebbackend.domain.QMember;
import com.twogather.twogatherwebbackend.domain.QReview;
import com.twogather.twogatherwebbackend.domain.QStore;
import com.twogather.twogatherwebbackend.dto.review.MyReviewInfoResponse;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import com.twogather.twogatherwebbackend.exception.SQLException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.domain.QImage.image;
import static com.twogather.twogatherwebbackend.domain.QMember.member;
import static com.twogather.twogatherwebbackend.domain.QReview.review;
import static com.twogather.twogatherwebbackend.domain.QStore.store;
import static com.twogather.twogatherwebbackend.exception.SQLException.SQLErrorCode.INVALID_REQUEST_PARAM;

@Repository
@AllArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<StoreDetailReviewResponse> findReviewsByStoreId(Long storeId, Pageable pageable) {
        QReview subReview = new QReview("subReview");
        QMember subMember = new QMember("subMember");

        List<StoreDetailReviewResponse> responseList = jpaQueryFactory
                .select(Projections.constructor(StoreDetailReviewResponse.class,
                        member.memberId,
                        review.reviewId,
                        review.content,
                        review.score,
                        review.createdDate,
                        member.username,
                        JPAExpressions.select(subReview.score.avg())
                                .from(subReview)
                                .join(subReview.reviewer, subMember)
                                .where(subMember.memberId.eq(member.memberId))
                        ))
                .from(review)
                .join(review.reviewer, member)
                .join(review.store, store)
                .where(store.storeId.eq(storeId))
                .orderBy(reviewSort(pageable))
                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(responseList, pageable, responseList.size());
    }

    @Override
    public Page<MyReviewInfoResponse> findMyReviewsByMemberId(Long memberId, Pageable pageable) {
        List<MyReviewInfoResponse> responseList = jpaQueryFactory
                .select(Projections.constructor(MyReviewInfoResponse.class,
                        review.reviewId,
                        review.content,
                        review.score,
                        review.createdDate,
                        image.url,
                        store.name,
                        store.address,
                        member.username))
                .from(review)
                .join(review.reviewer, member)
                .join(review.store, store)
                .leftJoin(image)
                .on(store.storeId.eq(image.store.storeId))
                .where(member.memberId.eq(memberId))
                .orderBy(reviewSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(responseList, pageable, responseList.size());
    }

    // 동적 정렬
    private OrderSpecifier<?> reviewSort(Pageable pageable) {
        if(!pageable.getSort().isEmpty()) {
            for(Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch(order.getProperty()) {
                    case "createdDate":
                        return new OrderSpecifier<>(direction, review.createdDate);
                    case "score":
                        return new OrderSpecifier<>(direction, review.score);
                    default:
                        throw new SQLException(INVALID_REQUEST_PARAM);
                }
            }
        }
        // 동적 정렬 skip
        return new OrderSpecifier(Order.ASC, NullExpression.DEFAULT, OrderSpecifier.NullHandling.Default);
    }
}
