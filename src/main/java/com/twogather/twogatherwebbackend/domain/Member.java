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
    private String password;
    private String name;
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    public Member(String email, String password, String name, AuthenticationType authenticationType, boolean isActive) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.authenticationType = authenticationType;
        this.isActive = isActive;
    }
    public Member(Long id, String email, String password, String name, AuthenticationType authenticationType, boolean isActive) {
        this.memberId = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.authenticationType = authenticationType;
        this.isActive = isActive;
    }

}
