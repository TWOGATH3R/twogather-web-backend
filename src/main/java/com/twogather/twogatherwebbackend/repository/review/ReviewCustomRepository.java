package com.twogather.twogatherwebbackend.repository.review;

import com.twogather.twogatherwebbackend.domain.Review;
import java.util.List;

public interface ReviewCustomRepository {
    List<String> findRandomNReviewContentList(int n);
}
