package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
