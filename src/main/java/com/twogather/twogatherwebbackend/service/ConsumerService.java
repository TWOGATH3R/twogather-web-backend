package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.*;
import static com.twogather.twogatherwebbackend.util.SecurityUtils.getLoginUsername;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsumerService {
    private final ConsumerRepository consumerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;

    public boolean isConsumer(final Long requestMemberId){
        String currentUsername = getLoginUsername();
        Member requestMember = consumerRepository.findActiveMemberById(requestMemberId).orElseThrow(
                ()-> new MemberException(ONLY_CONSUMER));
        if (!currentUsername.equals(requestMember.getUsername())) {
            throw new MemberException(NO_SUCH_MEMBER);
        }
        return true;
    }
    public void delete(final Long memberId){
        Consumer consumer = consumerRepository.findById(memberId).orElseThrow(
                ()-> new MemberException(NO_SUCH_MEMBER_ID)
        );
        consumer.leave();
    }

    public MemberResponse join(final MemberSaveRequest request){
        memberService.checkMemberOverlapBySave(request);
        Consumer consumer
                = new Consumer(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()),
                request.getName(), AuthenticationType.CONSUMER, true);
        Consumer storedConsumer = consumerRepository.save(consumer);

        return toResponse(storedConsumer);
    }

    @Transactional(readOnly = true)
    public MemberResponse getConsumerInfo(final Long memberId) {
        Consumer consumer = consumerRepository.findActiveMemberById(memberId).orElseThrow(
                ()->new MemberException(MemberException.MemberErrorCode.NO_SUCH_MEMBER_ID)
        );
        return toResponse(consumer);
    }
    private MemberResponse toResponse(Consumer consumer){
        return new MemberResponse(consumer.getMemberId(), consumer.getUsername(),consumer.getEmail(),consumer.getName());
    }
}
