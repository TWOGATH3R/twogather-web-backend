package com.twogather.twogatherwebbackend.repository.review;

import com.twogather.twogatherwebbackend.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
}
