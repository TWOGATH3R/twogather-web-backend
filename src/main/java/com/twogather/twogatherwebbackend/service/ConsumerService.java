package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.dto.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.dto.ConsumerSaveResponse;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsumerService {
    private final ConsumerRepository consumerRepository;

    public ConsumerSaveResponse save(final ConsumerSaveRequest request){
        validateDuplicateEmail(request.getEmail());
        Consumer consumer = new Consumer(request.getEmail(), request.getPassword(), request.getName(), request.getPhone());
        Consumer storedConsumer = consumerRepository.save(consumer);
        return new ConsumerSaveResponse(storedConsumer.getMemberId());
    }
    private void validateDuplicateEmail(final String email){
        if (consumerRepository.existsByEmail(email)) {
            throw new MemberException(MemberException.MemberErrorCode.DUPLICATE_EMAIL);
        }
    }
}
