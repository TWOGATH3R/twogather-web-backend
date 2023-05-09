package com.twogather.twogatherwebbackend.dto.member;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUser extends User {
    private Long memberId;
    private String email;
    private String name;

    public CustomUser(Long memberId, String email, String name,
                      String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.memberId = memberId;
        this.email = email;
        this.name = name;
    }
}
