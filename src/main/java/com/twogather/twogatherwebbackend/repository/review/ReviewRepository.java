package com.twogather.twogatherwebbackend.repository.review;

import com.twogather.twogatherwebbackend.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
    @Query("SELECT r.store.storeId, AVG(r.score) " +
            "FROM Review r " +
            "INNER JOIN r.store s " +
            "WHERE s.storeId IN (:storeIds) " +
            "GROUP BY r.store")
    List<Object[]> findAverageScoresByStoreIds(@Param("storeIds") List<Long> storeIds);
    @Query("SELECT r.store.storeId, COUNT(r) " +
            "FROM Review r " +
            "INNER JOIN r.store s " +
            "WHERE s.storeId IN (:storeIds) " +
            "GROUP BY r.store")
    List<Object[]> countReviewsByGivenStoreIds(@Param("storeIds") List<Long> storeIds);


    default Map<Long, Long> countReviewsByStoreIds(List<Long> storeIds) {
        List<Object[]> results = countReviewsByGivenStoreIds(storeIds);
        return results.stream()
                .collect(Collectors.toMap(
                        data -> (Long) data[0],
                        data -> (Long) data[1]
                ));
    }
    default Map<Long, Double> averageScoresByStoreIds(List<Long> storeIds) {
        List<Object[]> results = findAverageScoresByStoreIds(storeIds);
        return results.stream()
                .collect(Collectors.toMap(
                        data -> (Long) data[0],
                        data -> (Double) data[1]
                ));
    }
}
