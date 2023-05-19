package com.twogather.twogatherwebbackend.repository.store;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twogather.twogatherwebbackend.domain.QImage;
import com.twogather.twogatherwebbackend.domain.QReview;
import com.twogather.twogatherwebbackend.domain.StoreApprovalStatus;
import com.twogather.twogatherwebbackend.dto.store.TopStoreResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.domain.QStore.store;

@Repository
public class StoreCustomRepositoryImpl implements StoreCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public StoreCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    @Override
    public List<TopStoreResponse> findTopNByScore(int n) {
        QReview review = QReview.review;
        QImage image = QImage.image;
        List<Tuple> results = jpaQueryFactory
                .select(store.storeId,store.name, MathExpressions.round(review.score.avg(), 1), store.address, image.serverFileName)
                .from(store)
                .where(store.isApproved.eq(StoreApprovalStatus.APPROVED))
                .leftJoin(store.reviewList, review)
                .leftJoin(store.storeImageList, image)
                .groupBy(store.storeId)
                .orderBy(review.score.avg().desc())
                .limit(n)
                .fetch();

        return results.stream()
                .map(tuple -> new TopStoreResponse(
                        tuple.get(store.storeId),
                        tuple.get(store.name),
                        tuple.get(MathExpressions.round(review.score.avg(), 1)),
                        tuple.get(store.address),
                        tuple.get(image.serverFileName)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TopStoreResponse> findTopNByReviewCount(int n) {
        QReview review = QReview.review;
        QImage image = QImage.image;

        List<Tuple> results = jpaQueryFactory
                .select(store.storeId,store.name, MathExpressions.round(review.score.avg(), 1), store.address, image.serverFileName)
                .from(store)
                .where(store.isApproved.eq(StoreApprovalStatus.APPROVED))
                .leftJoin(store.reviewList, review)
                .leftJoin(store.storeImageList, image)
                .groupBy(store.storeId)
                .orderBy(review.count().desc())
                .limit(n)
                .fetch();

        return results.stream()
                .map(tuple -> new TopStoreResponse(
                        tuple.get(store.storeId),
                        tuple.get(store.name),
                        tuple.get(MathExpressions.round(review.score.avg(), 1)),
                        tuple.get(store.address),
                        tuple.get(image.serverFileName)
                ))
                .collect(Collectors.toList());
    }
}
