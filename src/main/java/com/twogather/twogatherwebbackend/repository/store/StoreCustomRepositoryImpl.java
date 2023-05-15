package com.twogather.twogatherwebbackend.repository.store;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twogather.twogatherwebbackend.domain.QImage;
import com.twogather.twogatherwebbackend.domain.QReview;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.store.TopStoreInfoResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.domain.QStore.store;
import static com.twogather.twogatherwebbackend.domain.QReview.review;

@Repository
public class StoreCustomRepositoryImpl implements StoreCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public StoreCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    @Override
    public List<TopStoreInfoResponse> findTopNByScore(int n) {
        QReview review = QReview.review;
        QImage image = QImage.image;
        List<Tuple> results = jpaQueryFactory
                .select(store.name, MathExpressions.round(review.score.avg(), 1), store.address, image.serverFileName)
                .from(store)
                .where(store.isApproved.isTrue())
                .leftJoin(store.reviewList, review)
                .leftJoin(store.storeImageList, image)
                .groupBy(store.storeId)
                .orderBy(review.score.avg().desc())
                .limit(n)
                .fetch();

        return results.stream()
                .map(tuple -> new TopStoreInfoResponse(
                        tuple.get(store.name),
                        tuple.get(MathExpressions.round(review.score.avg(), 1)),
                        tuple.get(store.address),
                        tuple.get(image.serverFileName)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TopStoreInfoResponse> findTopNByReviewCount(int n) {
        QReview review = QReview.review;
        QImage image = QImage.image;

        List<Tuple> results = jpaQueryFactory
                .select(store.name, MathExpressions.round(review.score.avg(), 1), store.address, image.serverFileName)
                .from(store)
                .where(store.isApproved.isTrue())
                .leftJoin(store.reviewList, review)
                .leftJoin(store.storeImageList, image)
                .groupBy(store.storeId)
                .orderBy(review.count().desc())
                .limit(n)
                .fetch();

        return results.stream()
                .map(tuple -> new TopStoreInfoResponse(
                        tuple.get(store.name),
                        tuple.get(MathExpressions.round(review.score.avg(), 1)),
                        tuple.get(store.address),
                        tuple.get(image.serverFileName)
                ))
                .collect(Collectors.toList());
    }
}
