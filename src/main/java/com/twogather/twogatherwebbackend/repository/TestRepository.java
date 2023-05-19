package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}
