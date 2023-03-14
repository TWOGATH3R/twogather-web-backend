package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@Getter
@Inheritance(strategy = TABLE_PER_CLASS)
public abstract class User {
    @Id
    @GeneratedValue
    @Column(name="user_id")
    private Long id;

    private String loginId;
    private String loginPw;
    private String name;
    private String phone;
    private String email;
}
