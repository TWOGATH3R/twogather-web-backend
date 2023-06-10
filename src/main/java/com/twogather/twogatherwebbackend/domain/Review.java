package com.twogather.twogatherwebbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="review_id")
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member reviewer;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "content", columnDefinition = "VARCHAR(5000)")
    private String content;
    private Double score;
    private LocalDate createdDate;

    public Review(Store store, Member reviewer, String content, Double score, LocalDate createdDate){
        this.store = store;
        this.reviewer = reviewer;
        this.content = content;
        this.score = score;
        this.createdDate = createdDate;
    }

    public void update(String content, Double score) {
        if(content != null && !content.isEmpty()) {
            this. content = content;
        }
        if(!score.isNaN() && 0.0 <= score && score <= 5.0) {
            this. content = content;
        }
    }
}
