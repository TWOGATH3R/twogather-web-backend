package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.dto.member.ConsumerResponse;
import com.twogather.twogatherwebbackend.dto.member.ConsumerSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsumerService {
    private final ConsumerRepository consumerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public boolean isConsumer(Long memberId){
        if(consumerRepository.findById(memberId).isPresent()) return true;
        else return false;
    }
    public void delete(final Long memberId){
        //TODO:구현
    }
    public ConsumerResponse update(final ConsumerSaveUpdateRequest request){
        //TODO: 구현
        return new ConsumerResponse();
    }
    public ConsumerResponse join(final ConsumerSaveUpdateRequest request){
        validateDuplicateEmail(request.getUsername());
        Consumer consumer
                = new Consumer(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()),
                request.getName(), AuthenticationType.CONSUMER, true);
        Consumer storedConsumer = consumerRepository.save(consumer);

        return toConsumerResponse(storedConsumer);
    }


    @Transactional(readOnly = true)
    public ConsumerResponse getConsumerInfo(final Long memberId) {
        Consumer consumer = consumerRepository.findById(memberId).orElseThrow(
                ()->new MemberException(MemberException.MemberErrorCode.NO_SUCH_MEMBER_ID)
        );
        return toConsumerResponse(consumer);
    }

    private void validateDuplicateEmail(final String username){
        if (memberRepository.existsByUsername(username)) {
            throw new MemberException(MemberException.MemberErrorCode.DUPLICATE_USERNAME);
        }
    }
    private ConsumerResponse toConsumerResponse(Consumer consumer){
        return new ConsumerResponse(consumer.getMemberId(), consumer.getUsername(),consumer.getName(), consumer.getEmail());
    }
}
