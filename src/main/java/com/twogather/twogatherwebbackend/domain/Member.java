package com.twogather.twogatherwebbackend.domain;

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

    public Member(String email, String loginPw, String name, String phone) {
        this.email = email;
        this.loginPw = loginPw;
        this.name = name;
        this.phone = phone;
    }
}
