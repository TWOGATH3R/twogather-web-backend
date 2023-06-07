package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_MEMBER;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_MEMBER_ID;

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
        Consumer consumer = consumerRepository.findById(memberId).orElseThrow(
                ()-> new MemberException(NO_SUCH_MEMBER_ID)
        );
        consumer.leave();
    }
    public MemberResponse update(final MemberSaveUpdateRequest request){
        Consumer consumer = consumerRepository.findByUsername(SecurityUtils.getUsername()).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        consumer.update(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName());

        return toResponse(consumer);
    }
    public MemberResponse join(final MemberSaveUpdateRequest request){
        validateDuplicateEmail(request.getUsername());
        Consumer consumer
                = new Consumer(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()),
                request.getName(), AuthenticationType.CONSUMER, true);
        Consumer storedConsumer = consumerRepository.save(consumer);

        return toResponse(storedConsumer);
    }
    public boolean verifyPassword(String password){
        String username = SecurityUtils.getUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                ()-> new MemberException(NO_SUCH_MEMBER)
        );
        return passwordEncoder.matches(password, member.getPassword());
    }

    @Transactional(readOnly = true)
    public MemberResponse getConsumerInfo(final Long memberId) {
        Consumer consumer = consumerRepository.findById(memberId).orElseThrow(
                ()->new MemberException(MemberException.MemberErrorCode.NO_SUCH_MEMBER_ID)
        );
        return toResponse(consumer);
    }

    private void validateDuplicateEmail(final String username){
        if (memberRepository.existsByUsername(username)) {
            throw new MemberException(MemberException.MemberErrorCode.DUPLICATE_USERNAME);
        }
    }
    private MemberResponse toResponse(Consumer consumer){
        return new MemberResponse(consumer.getMemberId(), consumer.getUsername(),consumer.getEmail(),consumer.getName());
    }
}
