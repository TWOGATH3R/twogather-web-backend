package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Likes;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.exception.*;
import com.twogather.twogatherwebbackend.repository.LikeRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException.AccessDeniedExceptionErrorCode.ACCESS_DENIED;
import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.UNAUTHORIZED;
import static com.twogather.twogatherwebbackend.exception.LikeException.LikeErrorCode.DUPLICATE_LIKE;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LikeService {
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final LikeRepository likeRepository;

    public void addStoreLike(Long storeId){
        String username = SecurityUtils.getUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                ()->new CustomAuthenticationException(UNAUTHORIZED)
        );
        Store store = storeRepository.findActiveStoreById(storeId).orElseThrow(
                ()->new StoreException(NO_SUCH_STORE)
        );
        if(likeRepository.findByStoreStoreIdAndMemberMemberId(member.getMemberId(), storeId).isPresent()){
            throw new LikeException(DUPLICATE_LIKE);
        }
        likeRepository.save(new Likes(store, member));
    }

    public void deleteStoreLike(Long storeId){
        String username = SecurityUtils.getUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                ()-> new CustomAuthenticationException(UNAUTHORIZED)
        );

        int deletedRows = likeRepository.deleteByStoreStoreIdAndMemberMemberId(storeId, member.getMemberId());

        if(deletedRows!=1){
            log.error("너무많거나 적은 like entity가 삭제되었습니다 storeId: {}, memberId: {}", storeId, member.getMemberId());

        }

    }
}
