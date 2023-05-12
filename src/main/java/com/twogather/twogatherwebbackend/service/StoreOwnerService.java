package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerResponse;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.dto.valid.BizRegNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.CustomAccessDeniedException.AccessDeniedExceptionErrorCode.ACCESS_DENIED;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.DUPLICATE_EMAIL;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_EMAIL;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreOwnerService {
    private final StoreOwnerRepository storeOwnerRepository;
    private final MemberRepository memberRepository;
    private final BizRegNumberValidator validator;
    private final PasswordEncoder passwordEncoder;

    public boolean isStoreOwner(Long memberId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Member member = memberRepository.findByEmail(currentUsername).orElseThrow(
                ()-> new MemberException(NO_SUCH_EMAIL)
        );
        if (!currentUsername.equals(member.getEmail())) {
            throw new CustomAccessDeniedException(ACCESS_DENIED);
        }
        return true;
    }

    public StoreOwnerResponse join(final StoreOwnerSaveUpdateRequest request){
        validateDuplicateEmail(request.getEmail());
        StoreOwner owner = new StoreOwner(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName(),
                request.getBusinessNumber(), request.getBusinessName(), request.getBusinessStartDate(), AuthenticationType.STORE_OWNER,true);
        StoreOwner storedOwner = storeOwnerRepository.save(owner);
        return toStoreOwnerResponse(storedOwner);
        
    }
    public void delete(Long id){
        //TODO:구현
    }
    public StoreOwnerResponse update(final StoreOwnerSaveUpdateRequest request){
        //TODO:구현
        return new StoreOwnerResponse();
    }

    @Transactional(readOnly = true)
    public StoreOwnerResponse getMemberWithAuthorities(String email){
        StoreOwner owner = findMemberByEmailOrElseThrow(email);

        return toStoreOwnerResponse(owner);
    }
    @Transactional(readOnly = true)
    public StoreOwnerResponse getMemberWithAuthorities(Long id){
        //TODO: 구현

        return new StoreOwnerResponse();
    }


    @Transactional(readOnly = true)
    public StoreOwnerResponse getMemberWithAuthorities(){
        /*
        Optional<StoreOwner> optionalOwner = SecurityUtils.getCurrentUsername().flatMap(storeOwnerRepository::findByEmail);
        optionalOwner.orElseThrow(()-> new MemberException(NO_SUCH_EMAIL));
        StoreOwner owner = optionalOwner.get();
        return toStoreOwnerResponse(owner);*/
        return null;
    }

    public StoreOwner findMemberByEmailOrElseThrow(final String email){
        return storeOwnerRepository.findByEmail(email).orElseThrow(()-> new MemberException(NO_SUCH_EMAIL));
    }
    public void validateDuplicateEmail(final String email){
        if (storeOwnerRepository.existsByEmail(email)) {
            throw new MemberException(DUPLICATE_EMAIL);
        }
    }
    private StoreOwnerResponse toStoreOwnerResponse(StoreOwner owner){
        return new StoreOwnerResponse(owner.getMemberId(), owner.getName(), owner.getEmail(),
                owner.getBusinessNumber(), owner.getBusinessName(), owner.getBusinessStartDate());
    }
}
