package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.dto.member.ConsumerSaveRequest;
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

    public void save(final ConsumerSaveRequest request){
        validateDuplicateEmail(request.getEmail());
        Consumer consumer = new Consumer(request.getEmail(), request.getPassword(), request.getName(), request.getPhone());
        consumerRepository.save(consumer);
    }
    private void validateDuplicateEmail(final String email){
        if (consumerRepository.existsByEmail(email)) {
            throw new MemberException(MemberException.MemberErrorCode.DUPLICATE_EMAIL);
        }
    }
}
