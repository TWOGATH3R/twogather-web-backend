package com.twogather.twogatherwebbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    protected Long memberId;

    @Column(unique = true, length = 20)
    protected String username;
    @Column(unique = true, length = 30)
    protected String email;
    protected String password;
    @Column(length = 10)
    protected String name;
    protected boolean isActive;

    @Enumerated(EnumType.STRING)
    protected AuthenticationType authenticationType;

    public Member(String username, String email, String password, String name, AuthenticationType authenticationType, boolean isActive) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.authenticationType = authenticationType;
        this.isActive = isActive;
    }
    public Member(Long id, String username, String email, String password, String name, AuthenticationType authenticationType, boolean isActive) {
        this.username = username;
        this.memberId = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.authenticationType = authenticationType;
        this.isActive = isActive;
    }
    public void update(String username, String email, String password, String name){
        if(!username.isEmpty()){
            this.username = username;
        }
        if(!email.isEmpty()){
            this.email = email;
        }
        if(!password.isEmpty()){
            this.password = password;
        }
        if(!name.isEmpty()){
            this.name = name;
        }
    }
    public void leave(){
        this.isActive = false;
    }

}
