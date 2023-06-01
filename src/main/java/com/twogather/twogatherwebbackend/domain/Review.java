package com.twogather.twogatherwebbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate  // 변경된 필드만 반영되도록 설정
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

    //이거 string으로 해도되는건가
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

    public void updateContent(String content) {
        if(content != null && !content.isEmpty()) {
            this. content = content;
        }
    }

    public void updateScore(Double score) {
        if(!score.isNaN() && 0.0 <= score && score <= 5.0) {
            this. content = content;
        }
    }
}
