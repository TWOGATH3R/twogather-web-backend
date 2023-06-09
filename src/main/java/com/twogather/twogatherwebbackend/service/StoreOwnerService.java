package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException;
import com.twogather.twogatherwebbackend.exception.CustomAuthenticationException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException.AccessDeniedExceptionErrorCode.ACCESS_DENIED;
import static com.twogather.twogatherwebbackend.exception.CustomAuthenticationException.AuthenticationExceptionErrorCode.UNAUTHORIZED;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreOwnerService {
    private final StoreOwnerRepository storeOwnerRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isStoreOwner(Long memberId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Member member = memberRepository.findActiveMemberByUsername(currentUsername).orElseThrow(
                ()->  new CustomAuthenticationException(UNAUTHORIZED));
        if (!currentUsername.equals(member.getUsername())) {
            throw new CustomAccessDeniedException(ACCESS_DENIED);
        }
        return true;
    }


    public MemberResponse join(final MemberSaveUpdateRequest request){
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
    public MemberResponse getMemberWithAuthorities(Long id){
        //TODO: 구현

        return new MemberResponse();
    }


    @Transactional(readOnly = true)
    public MemberResponse getMemberWithAuthorities(){
        /*
        Optional<StoreOwner> optionalOwner = SecurityUtils.getCurrentUsername().flatMap(storeOwnerRepository::findByEmail);
        optionalOwner.orElseThrow(()-> new MemberException(NO_SUCH_EMAIL));
        StoreOwner owner = optionalOwner.get();
        return toStoreOwnerResponse(owner);*/
        return null;
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
