package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.*;
import static com.twogather.twogatherwebbackend.util.SecurityUtils.getLoginUsername;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreOwnerService {
    private final StoreOwnerRepository storeOwnerRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isStoreOwner(Long requestMemberId){
        String currentUsername = getLoginUsername();
        Member requestMember = storeOwnerRepository.findActiveMemberById(requestMemberId).orElseThrow(
                ()-> new MemberException(NO_SUCH_MEMBER));
        if (!currentUsername.equals(requestMember.getUsername())) {
            throw new MemberException(NO_SUCH_MEMBER);
        }
        return true;
    }


    public MemberResponse join(final MemberSaveRequest request){
        validateDuplicateUsername(request.getUsername());
        StoreOwner owner = new StoreOwner(
                request.getUsername(),
                request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName(), AuthenticationType.STORE_OWNER,true);
        StoreOwner storedOwner = storeOwnerRepository.save(owner);
        return toStoreOwnerResponse(storedOwner);
        
    }
    public void delete(Long id){
        StoreOwner owner = storeOwnerRepository.findById(id).orElseThrow(
                ()-> new MemberException(NO_SUCH_MEMBER_ID)
        );
        for (Store store:owner.getStoreList()) {
            store.delete();
        }
        owner.leave();
    }

    @Transactional(readOnly = true)
    public MemberResponse getOwnerInfo(Long memberId){
        StoreOwner owner = storeOwnerRepository.findActiveMemberById(memberId).orElseThrow(
                ()->new MemberException(MemberException.MemberErrorCode.NO_SUCH_MEMBER_ID)
        );
        return toStoreOwnerResponse(owner);
    }

    public void validateDuplicateUsername(final String username){
        if (memberRepository.existsByActiveUsername(username)) {
            throw new MemberException(DUPLICATE_USERNAME);
        }
    }
    private MemberResponse toStoreOwnerResponse(StoreOwner owner){
        return new MemberResponse(owner.getMemberId(),
                owner.getUsername(),
                owner.getEmail(),
                owner.getName());
    }
}
