package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
