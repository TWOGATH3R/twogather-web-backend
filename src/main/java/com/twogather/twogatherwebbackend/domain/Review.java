package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="review_id")
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Consumer reviewer;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "content", columnDefinition = "VARCHAR(5000)")
    private String content;
    private Double score;
    private LocalDate createdDate;

    public Review(Store store, Consumer reviewer, String content, Double score, LocalDate createdDate){
        this.store = store;
        this.reviewer = reviewer;
        this.content = content;
        this.score = score;
        this.createdDate = createdDate;
    }

}
