package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    int deleteByStoreStoreIdAndMemberMemberId(Long storeId, Long memberId);
    @Query("SELECT l FROM Likes l WHERE l.store.storeId = :storeId AND l.member.memberId = :memberId")
    Optional<Likes> findByStoreIdAndMemberId(@Param("storeId") Long storeId, @Param("memberId") Long memberId);
    @Query("SELECT s.storeId, COUNT(l) " +
            "FROM Likes l " +
            "INNER JOIN l.store s " +
            "WHERE s.storeId IN (:storeIds) " +
            "GROUP BY l.store")
    List<Object[]> countLikesByGivenStoreIds(@Param("storeIds") List<Long> storeIds);

    default Map<Long, Long> countLikesByStoreIds(List<Long> storeIds) {
        List<Object[]> results = countLikesByGivenStoreIds(storeIds);
        return results.stream()
                .collect(Collectors.toMap(
                        data -> (Long) data[0],
                        data -> (Long) data[1]
                ));
    }

}
