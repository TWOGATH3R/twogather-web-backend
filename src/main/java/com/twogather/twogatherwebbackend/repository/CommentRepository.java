package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
