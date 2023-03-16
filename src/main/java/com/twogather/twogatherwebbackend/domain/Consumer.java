package com.twogather.twogatherwebbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Consumer extends User{
    @Id
    @GeneratedValue
    @Column(name="consumer_id")
    private Long id;

    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviewList = new ArrayList<>();

    public Consumer(String email, String loginPw, String name, String phone) {
        super(email, loginPw, name, phone);
    }
}
