package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse update(final MemberSaveUpdateRequest request){
        String originUsername = SecurityUtils.getUsername();
        if(request.getUsername()!=originUsername && memberRepository.findActiveMemberByUsername(request.getUsername()).isPresent()){
            throw new MemberException(DUPLICATE_USERNAME);
        }
        Member member = memberRepository.findActiveMemberByUsername(SecurityUtils.getUsername()).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        String originEmail = member.getEmail();
        if(request.getEmail()!=originEmail && memberRepository.findActiveMemberByEmail(request.getEmail()).isPresent()){
            throw new MemberException(DUPLICATE_EMAIL);
        }
        member.update(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName());

        return toResponse(member);
    }
    public boolean verifyPassword(String password){
        String username = SecurityUtils.getUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                ()-> new MemberException(NO_SUCH_MEMBER)
        );
        return passwordEncoder.matches(password, member.getPassword());
    }

    private MemberResponse toResponse(Member member){
        return new MemberResponse(member.getMemberId(), member.getUsername(),member.getEmail(),member.getName());
    }
}
