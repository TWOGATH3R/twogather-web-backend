package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    // todo AccessToken이 담긴 Dto를 반환
    @Transactional(readOnly = true)
    public void login(LoginRequest loginRequest) {
        Member findMember = findMemberByEmailOrElseThrow(loginRequest.getEmail());

        validatePassword(findMember, loginRequest.getPassword());
    }

    private Member findMemberByEmailOrElseThrow(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberException.MemberErrorCode.NO_SUCH_EMAIL));
    }

    private void validatePassword(Member findMember, String password) {
        if (!findMember.getLoginPw().equals(password)) {
            throw new MemberException(MemberException.MemberErrorCode.PASSWORD_MISMATCH);
        }
    }
}