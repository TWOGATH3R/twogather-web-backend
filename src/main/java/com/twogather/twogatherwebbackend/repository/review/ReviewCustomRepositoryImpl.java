package com.twogather.twogatherwebbackend.repository.review;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twogather.twogatherwebbackend.domain.QReview;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Repository
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;
    private QReview review = QReview.review;

    public ReviewCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    @Override
    public Double findAvgScoreByStoreId(Long storeId) {
        Double avgScore = jpaQueryFactory.select(review.score.avg())
                .from(review)
                .where(review.store.storeId.eq(storeId))
                .fetchOne();

        if (avgScore == null) {
            return null;
        } else {
            BigDecimal bd = BigDecimal.valueOf(avgScore);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }
}
