package com.twogather.twogatherwebbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long memberId;

    private String email;
    private String loginPw;
    private String name;
    private String phone;
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    public Member(String email, String loginPw, String name, String phone, AuthenticationType authenticationType, boolean isActive) {
        this.email = email;
        this.loginPw = loginPw;
        this.name = name;
        this.phone = phone;
        this.authenticationType = authenticationType;
        this.isActive = isActive;
    }
    public Member(Long id, String email, String loginPw, String name, String phone, AuthenticationType authenticationType, boolean isActive) {
        this.memberId = id;
        this.email = email;
        this.loginPw = loginPw;
        this.name = name;
        this.phone = phone;
        this.authenticationType = authenticationType;
        this.isActive = isActive;
    }

}
