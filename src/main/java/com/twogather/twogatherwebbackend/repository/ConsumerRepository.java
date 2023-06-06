package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    //소비자가 작성한 리뷰의 평균값 계산하기
    @Query("select avg(r.score) from Review r group by r.reviewId having r.reviewId = :id")
    double getAverageReviewScore(Long id);
    boolean existsByUsername(String username);
    Optional<Consumer> findByUsername(String username);
}
