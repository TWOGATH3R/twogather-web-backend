package com.twogather.twogatherwebbackend.dto.member;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUser extends User {
    private Long memberId;
    private String username;
    private String name;
    private String role;

    public CustomUser(Member member){
        super(member.getUsername(), member.getPassword(), toGrantedAuthority(member.getAuthenticationType()));
        role = member.getAuthenticationType().name();
        this.username = member.getUsername();
        this.memberId = member.getMemberId();
        this.name = member.getName();
    }
    static Collection<? extends GrantedAuthority> toGrantedAuthority(AuthenticationType type){
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority(type.authority()));
        return grantedAuthorityList;
    }
    public CustomUser(Long memberId, String username, String name, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.memberId = memberId;
        this.username = username;
        this.name = name;
        this.role = authorities.toArray()[0].toString();
    }
}
