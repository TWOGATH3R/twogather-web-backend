package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Consumer extends User{
    @Id
    @GeneratedValue
    @Column(name="consumer_id")
    private Long id;

    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviewList = new ArrayList<>();

    private Double averageReviewScore;

}
