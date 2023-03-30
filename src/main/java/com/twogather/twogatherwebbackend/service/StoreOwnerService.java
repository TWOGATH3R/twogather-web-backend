package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerInfoResponse;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public void join(final StoreOwnerSaveRequest request){
        validateDuplicateEmail(request.getEmail());
        validator.validateBizRegNumber(request.getBusinessNumber(), request.getBusinessStartDate(), request.getBusinessName());
        StoreOwner owner = new StoreOwner(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName(), request.getPhone(),
                request.getBusinessNumber(), request.getBusinessName(), stringToLocalDate(request.getBusinessStartDate()), AuthenticationType.OWNER,true);
        storeOwnerRepository.save(owner);
    }

    @Transactional(readOnly = true)
    public StoreOwnerInfoResponse getMemberWithAuthorities(String email){
        StoreOwner owner = findMemberByEmailOrElseThrow(email);

        return new StoreOwnerInfoResponse(owner.getName(), owner.getEmail(), owner.getPhone(),
                owner.getBusinessNumber(), owner.getBusinessName(), owner.getBusinessStartDate());
    }
    @Transactional(readOnly = true)
    public StoreOwnerInfoResponse getMemberWithAuthorities(){
        Optional<StoreOwner> optionalOwner = SecurityUtils.getCurrentUsername().flatMap(storeOwnerRepository::findByEmail);
        optionalOwner.orElseThrow(()-> new MemberException(NO_SUCH_EMAIL));
        StoreOwner owner = optionalOwner.get();
        return new StoreOwnerInfoResponse(owner.getName(), owner.getEmail(), owner.getPhone(),
                owner.getBusinessNumber(), owner.getBusinessName(), owner.getBusinessStartDate());
    }


    private LocalDate stringToLocalDate(String date){
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    public StoreOwner findMemberByEmailOrElseThrow(final String email){
        return storeOwnerRepository.findByEmail(email).orElseThrow(()-> new MemberException(NO_SUCH_EMAIL));
    }
    public void validateDuplicateEmail(final String email){
        if (storeOwnerRepository.existsByEmail(email)) {
            throw new MemberException(DUPLICATE_EMAIL);
        }
    }
}
