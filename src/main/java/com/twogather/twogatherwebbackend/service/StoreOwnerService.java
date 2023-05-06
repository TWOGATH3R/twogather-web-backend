package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerResponse;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import com.twogather.twogatherwebbackend.dto.valid.BizRegNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.DUPLICATE_EMAIL;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_EMAIL;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreOwnerService {
    private final StoreOwnerRepository storeOwnerRepository;
    private final BizRegNumberValidator validator;
    private final PasswordEncoder passwordEncoder;

    public StoreOwnerResponse join(final StoreOwnerSaveUpdateRequest request){
        validateDuplicateEmail(request.getEmail());
        validator.validateBizRegNumber(request.getBusinessNumber(), request.getBusinessStartDate(), request.getBusinessName());
        StoreOwner owner = new StoreOwner(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName(),
                request.getBusinessNumber(), request.getBusinessName(), request.getBusinessStartDate(), AuthenticationType.OWNER,true);
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
        Optional<StoreOwner> optionalOwner = SecurityUtils.getCurrentUsername().flatMap(storeOwnerRepository::findByEmail);
        optionalOwner.orElseThrow(()-> new MemberException(NO_SUCH_EMAIL));
        StoreOwner owner = optionalOwner.get();
        return toStoreOwnerResponse(owner);
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
