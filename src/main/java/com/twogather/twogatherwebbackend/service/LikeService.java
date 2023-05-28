package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Likes;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.exception.LikeException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.LikeRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.LikeException.LikeErrorCode.DUPLICATE_LIKE;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_EMAIL;
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
        String loginUserEmail = SecurityUtils.getLoginUserEmail();
        Member member = memberRepository.findByEmail(loginUserEmail).orElseThrow(
                ()->new MemberException(NO_SUCH_EMAIL)
        );
        Store store = storeRepository.findById(storeId).orElseThrow(
                ()->new StoreException(NO_SUCH_STORE)
        );
        if(likeRepository.findByStoreStoreIdAndMemberMemberId(member.getMemberId(), storeId).isPresent()){
            throw new LikeException(DUPLICATE_LIKE);
        }
        likeRepository.save(new Likes(store, member));
    }

    public void deleteStoreLike(Long storeId){
        String loginUserEmail = SecurityUtils.getLoginUserEmail();
        Member member = memberRepository.findByEmail(loginUserEmail).orElseThrow(
                ()->new MemberException(NO_SUCH_EMAIL)
        );

        int deletedRows = likeRepository.deleteByStoreStoreIdAndMemberMemberId(storeId, member.getMemberId());

        if(deletedRows!=1){
            log.error("너무많거나 적은 like entity가 삭제되었습니다 storeId: {}, memberId: {}", storeId, member.getMemberId());

        }

    }
}
