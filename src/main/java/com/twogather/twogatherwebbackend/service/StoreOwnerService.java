package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveResponse;
import com.twogather.twogatherwebbackend.exception.UserException;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreOwnerService {
    private final StoreOwnerRepository storeOwnerRepository;
    private final BizRegNumberValidator validator;

    public StoreOwnerSaveResponse save(final StoreOwnerSaveRequest request){
        validateDuplicateEmail(request.getEmail());
        validator.validateBizRegNumber(request.getBusinessNumber(), request.getBusinessStartDate(), request.getBusinessName());
        StoreOwner owner = new StoreOwner(request.getEmail(), request.getPassword(), request.getName(), request.getPhone(),
                request.getBusinessNumber(), request.getBusinessName(), stringToLocalDate(request.getBusinessStartDate()));
        StoreOwner storedOwner = storeOwnerRepository.save(owner);
        return new StoreOwnerSaveResponse(storedOwner.getUserId());
    }

    private LocalDate stringToLocalDate(String date){
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    public void validateDuplicateEmail(final String email){
        if (storeOwnerRepository.existsByEmail(email)) {
            throw new UserException(UserException.UserErrorCode.DUPLICATE_EMAIL);
        }
    }
}
