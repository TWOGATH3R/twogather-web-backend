package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.dto.comment.CommentResponse;
import com.twogather.twogatherwebbackend.dto.comment.CommentSaveUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    public CommentResponse save(CommentSaveUpdateRequest request){
        //TODO:구현
        return null;
    }
    public CommentResponse update(CommentSaveUpdateRequest request){
        //TODO:구현
        return null;
    }
    public Boolean isMyComment(Long commentId){
        //TODO:구현
        return false;
    }
}
