package com.twogather.twogatherwebbackend.repository.review;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twogather.twogatherwebbackend.domain.QReview;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StoreDetailReviewResponse> findReviewsByStoreId(Long storeId, Pageable pageable) {
        /* TODO
                select *
                from review inner join (
                    select member_id, avg(score)
                    from review
                    group by member_id
                ) as avg_score
                on review.member_id = avg_score.member_id
                where store_id = 33
         */

        /* TODO
            QueryDSL은 from절 subquery를 지원하지 않음
            위 SQL문을 from절 subquery를 사용하지 않고 join만 사용하는 SQL문으로 변경
            SELECT a.review_id, a.content, a.created_date, a.score, a.member_id, a.store_id, b.member_id, avg(b.score)
            FROM REVIEW as a
            left join review as b
            on a.member_id = b.member_id
            group by b.member_id, a.review_id, a.content, a.created_date, a.score, a.member_id, a.store_id
            having a.store_id = :storeId;
         */

        QReview a = QReview.review;
        QReview b = new QReview("b");

        List<Tuple> results = jpaQueryFactory
                .select(a.reviewId, a.content, a.createdDate, a.score,
                        a.reviewer.memberId, a.reviewer.name, a.store.storeId, b.score.avg())
                .from(a)
                .leftJoin(b)
                .on(a.reviewer.memberId.eq(b.reviewer.memberId))
                .groupBy(b.reviewer.memberId, a.reviewId, a.content, a.createdDate,
                        a.score, a.reviewer.memberId, a.reviewer.name, a.store.storeId)
                .having(a.store.storeId.eq(storeId))
                .fetch();


        List<StoreDetailReviewResponse> responseList = results.stream()
                .map(tuple -> new StoreDetailReviewResponse(
                        tuple.get(a.reviewer.memberId),
                        tuple.get(a.reviewId),
                        tuple.get(a.content),
                        tuple.get(a.score),
                        tuple.get(a.createdDate),
                        tuple.get(a.reviewer.name),
                        tuple.get(b.score.avg())
                ))
                .collect(Collectors.toList());

        return responseList;
    }
}
