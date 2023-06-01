package com.twogather.twogatherwebbackend.repository.review;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twogather.twogatherwebbackend.domain.QReview;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public ReviewCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<String> findRandomNReviewContentList(int n) {
        QReview qReview = QReview.review;

        return jpaQueryFactory
                .select(qReview.content)
                .from(qReview)
                .orderBy(qReview.reviewId.asc())
                .fetch()
                .stream()
                .limit(n)
                .collect(Collectors.toList());
    }
}
