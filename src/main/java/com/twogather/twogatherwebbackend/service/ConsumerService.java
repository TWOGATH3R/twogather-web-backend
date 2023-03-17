package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.dto.ConsumerSaveResponse;
import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.dto.StoreOwnerSaveResponse;
import com.twogather.twogatherwebbackend.exception.UserException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
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
public class ConsumerService {
    private final ConsumerRepository consumerRepository;

    public ConsumerSaveResponse save(final ConsumerSaveRequest request){
        validateDuplicateEmail(request.getEmail());
        Consumer consumer = new Consumer(request.getEmail(), request.getPassword(), request.getName(), request.getPhone());
        Consumer storedConsumer = consumerRepository.save(consumer);
        return new ConsumerSaveResponse(storedConsumer.getUserId());
    }
    private void validateDuplicateEmail(final String email){
        if (consumerRepository.existsByEmail(email)) {
            throw new UserException(UserException.UserErrorCode.DUPLICATE_EMAIL);
        }
    }
}
