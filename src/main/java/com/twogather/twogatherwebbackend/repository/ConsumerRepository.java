package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    //소비자가 작성한 리뷰의 평균값 계산하기
    @Query("select avg(r.score) from Review r group by r.reviewId having r.reviewId = :id")
    double getAverageReviewScore(Long id);
    boolean existsByUsername(String username);
    Optional<Consumer> findByUsername(String username);
    @Query("select m from Consumer m where m.username =:username and m.isActive = true")
    Optional<Consumer> findActiveMemberByUsername(@Param("username") String username);
    @Query("select m from Consumer m where m.memberId =:memberId and m.isActive = true")
    Optional<Consumer> findActiveMemberById(@Param("memberId") Long memberId);

}
