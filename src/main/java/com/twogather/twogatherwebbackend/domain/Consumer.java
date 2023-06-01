package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue("consumer")
public class Consumer extends Member {
    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviewList = new ArrayList<>();

    public Consumer(String username, String email, String password, String name, AuthenticationType authenticationType, boolean isActive) {
        super(username, email, password, name, authenticationType, isActive);
    }
}
