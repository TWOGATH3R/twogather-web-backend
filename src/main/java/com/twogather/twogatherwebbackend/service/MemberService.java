package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberUpdateRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.*;
import static com.twogather.twogatherwebbackend.util.SecurityUtils.getLoginUsername;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse update(final MemberUpdateRequest request){
        String originUsername = getLoginUsername();
        if(request.getUsername()!=originUsername && memberRepository.findActiveMemberByUsername(request.getUsername()).isPresent()){
            throw new MemberException(DUPLICATE_USERNAME);
        }
        Member member = memberRepository.findActiveMemberByUsername(getLoginUsername()).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        String originEmail = member.getEmail();
        if(request.getEmail()!=originEmail && memberRepository.findActiveMemberByEmail(request.getEmail()).isPresent()){
            throw new MemberException(DUPLICATE_EMAIL);
        }
        member.update(
                request.getUsername(),
                request.getEmail(),
                "",
                request.getName());

        return toResponse(member);
    }
    public void changePassword(String password){
        String username = getLoginUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                ()-> new MemberException(NO_SUCH_MEMBER)
        );
        member.update("","",passwordEncoder.encode(password),"");
    }
    public boolean verifyPassword(String password){
        String username = getLoginUsername();
        Member member = memberRepository.findActiveMemberByUsername(username).orElseThrow(
                ()-> new MemberException(NO_SUCH_MEMBER)
        );
        return passwordEncoder.matches(password, member.getPassword());
    }
    public boolean existsEmail(String email){
        return memberRepository.findActiveMemberByEmail(email).isPresent();
    }

    private MemberResponse toResponse(Member member){
        return new MemberResponse(member.getMemberId(), member.getUsername(),member.getEmail(),member.getName());
    }
    private String getEncodedPassword(String password){
        if(password==null || password.isEmpty()) return null;
        else return passwordEncoder.encode(password);
    }
}
