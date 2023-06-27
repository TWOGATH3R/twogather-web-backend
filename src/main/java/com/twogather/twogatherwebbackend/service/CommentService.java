package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Comment;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.comment.CommentResponse;
import com.twogather.twogatherwebbackend.dto.comment.CommentSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.CommentException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.CommentRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.CommentException.CommentErrorCode.NO_SUCH_COMMENT;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_MEMBER;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    public CommentResponse save(Long reviewId, CommentSaveUpdateRequest request){
        //TODO: Review exception 추가
        String username = SecurityUtils.getLoginUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        Review review = reviewRepository.findById(reviewId).get();
        Comment comment = new Comment(request.getContent(), review, member);
        Comment savedComment = commentRepository.save(comment);
        return new CommentResponse(savedComment.getCommentId(), savedComment.getContent(),  savedComment.getCreatedDate());
    }
    public CommentResponse update(Long commentId, CommentSaveUpdateRequest request){
        String username = SecurityUtils.getLoginUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()->new CommentException(NO_SUCH_COMMENT)
        );
        comment.update(request.getContent());
        return new CommentResponse(comment.getCommentId(), comment.getContent(), comment.getCreatedDate());
    }
    public void delete(Long commentId){
        commentRepository.deleteById(commentId);
    }
    public Boolean isMyComment(Long commentId){
        String username = SecurityUtils.getLoginUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()->new CommentException(NO_SUCH_COMMENT)
        );
        return (member.getMemberId() == comment.getCommenter().getMemberId());
    }
}
