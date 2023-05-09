package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
