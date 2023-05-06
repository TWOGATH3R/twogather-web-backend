package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.auth.TokenProvider;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_EMAIL;
import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.PASSWORD_MISMATCH;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional(readOnly = true)
    public TokenAndId login(LoginRequest loginRequest) {
        Member findMember = findMemberByEmailOrElseThrow(loginRequest.getEmail());
        validatePassword(findMember.getPassword(), loginRequest.getPassword());

        // 인증 처리
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 발급
        String token = tokenProvider.createToken(authentication, findMember);
        return new TokenAndId(token, findMember.getMemberId());
    }

    private Member findMemberByEmailOrElseThrow(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(NO_SUCH_EMAIL));
    }

    private void validatePassword(String findMemberPassword, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, findMemberPassword)) {
            throw new MemberException(PASSWORD_MISMATCH);
        }
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenAndId{
        private String token;
        private Long id;
    }
}