package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = TABLE_PER_CLASS)
public abstract class User {
    @Id
    @GeneratedValue
    @Column(name="user_id")
    private Long userId;

    private String email;
    private String loginPw;
    private String name;
    private String phone;

    public User(String email, String loginPw, String name, String phone) {
        this.email = email;
        this.loginPw = loginPw;
        this.name = name;
        this.phone = phone;
    }
}
