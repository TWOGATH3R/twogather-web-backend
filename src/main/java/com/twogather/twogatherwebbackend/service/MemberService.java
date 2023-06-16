package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.email.EmailRequest;
import com.twogather.twogatherwebbackend.dto.member.FindUsernameRequest;
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
        if(!request.getUsername().equals(originUsername) && memberRepository.findActiveMemberByUsername(request.getUsername()).isPresent()){
            throw new MemberException(DUPLICATE_USERNAME);
        }
        Member member = memberRepository.findActiveMemberByUsername(getLoginUsername()).orElseThrow(
                ()->new MemberException(NO_SUCH_MEMBER)
        );
        String originEmail = member.getEmail();
        if(!request.getEmail().equals(originEmail) && memberRepository.findActiveMemberByEmail(request.getEmail()).isPresent()){
            throw new MemberException(DUPLICATE_EMAIL);
        }
        member.update(
                request.getUsername(),
                request.getEmail(),
                "",
                request.getName());

        return toResponse(member);
    }
    public String findMyUsername(FindUsernameRequest request){
        Member member = memberRepository.findActiveMemberByEmail(request.getEmail()).orElseThrow(()->new MemberException(NO_SUCH_MEMBER));
        if(!member.getName().equals(request.getName())) throw new MemberException(NO_SUCH_MEMBER_ID);

        return encodeUsername(member.getUsername());
    }
    public Boolean isExist(EmailRequest request){
        if(memberRepository.findActiveMemberByEmail(request.getEmail()).isPresent()) return true;
        else return false;
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
    public boolean isMyId(Long memberId){
        String currentUsername = getLoginUsername();
        Member requestMember = memberRepository.findActiveMemberById(memberId).orElseThrow(
                ()-> new MemberException(NO_SUCH_MEMBER));
        if (!currentUsername.equals(requestMember.getUsername())) {
            throw new MemberException(NO_SUCH_MEMBER);
        }
        return true;
    }
    public boolean existsEmail(String email){
        return memberRepository.findActiveMemberByEmail(email).isPresent();
    }

    private MemberResponse toResponse(Member member){
        return new MemberResponse(member.getMemberId(), member.getUsername(),member.getEmail(),member.getName());
    }
    private String encodeUsername(String username){
        if (username.length() >= 3) {
            int startIndex = username.length() - 3;
            String encodedUsername = username.substring(0, startIndex) + "***";
            return encodedUsername;
        }
        return username;
    }
}
