package com.twogather.twogatherwebbackend.repository.review;

import com.twogather.twogatherwebbackend.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

}
