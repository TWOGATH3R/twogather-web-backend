package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.member.CustomUser;
import com.twogather.twogatherwebbackend.exception.MemberException;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;


import static com.twogather.twogatherwebbackend.exception.MemberException.MemberErrorCode.NO_SUCH_MEMBER;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        return memberRepository.findActiveMemberByUsername(username)
                .map(member -> createUser(member))
                .orElseThrow(()-> new MemberException(NO_SUCH_MEMBER));
    }
    public CustomUser createUser(Member member){
        if (!member.isActive()){
            throw new MemberException(NO_SUCH_MEMBER);
        }
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority(member.getAuthenticationType().authority()));
        return new CustomUser(member.getMemberId(), member.getUsername(), member.getName(),
               member.getPassword(), grantedAuthorityList);
    }
}
