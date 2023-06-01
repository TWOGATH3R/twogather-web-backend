package com.twogather.twogatherwebbackend.repository.review;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twogather.twogatherwebbackend.domain.QImage;
import com.twogather.twogatherwebbackend.domain.QReview;
import com.twogather.twogatherwebbackend.dto.review.MyReviewInfoResponse;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import lombok.AllArgsConstructor;
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
    public Page<StoreDetailReviewResponse> findReviewsByStoreId(Long storeId, Pageable pageable) {
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

        List<StoreDetailReviewResponse> responseList = results.stream().map(tuple -> new StoreDetailReviewResponse(
                tuple.get(a.reviewer.memberId),
                tuple.get(a.reviewId),
                tuple.get(a.content),
                tuple.get(a.score),
                tuple.get(a.createdDate),
                tuple.get(a.reviewer.name),
                tuple.get(b.score.avg())
        )).collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, responseList.size());
    }

    @Override
    public Page<MyReviewInfoResponse> findMyReviewsByMemberId(Long memberId, Pageable pageable) {
        QReview review = QReview.review;
        QImage image = new QImage("image");

        List<Tuple> result = jpaQueryFactory
                .select(review, image.url)
                .from(review)
                .leftJoin(image)
                .on(review.store.storeId.eq(image.store.storeId))
                .where(review.reviewer.memberId.eq(memberId))
                .fetch();

        List<MyReviewInfoResponse> responseList = result.stream().map(tuple -> new MyReviewInfoResponse(
                tuple.get(review.reviewId),
                tuple.get(review.content),
                tuple.get(review.score),
                tuple.get(review.createdDate),
                tuple.get(image.url),
                tuple.get(review.store.name),
                tuple.get(review.store.address),
                tuple.get(review.reviewer.name)
        )).collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, responseList.size());
    }
}
