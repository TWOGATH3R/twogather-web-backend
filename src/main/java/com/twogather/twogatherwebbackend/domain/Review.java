package com.twogather.twogatherwebbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(mappedBy = "review")
    private Comment comment;

    @Column(name = "content", columnDefinition = "VARCHAR(5000)")
    private String content;
    private Double score;
    private LocalDateTime createdDate;

    public void addStore(Store store) {
        this.store = store;
        this.store.addReview(this);
    }
    public void addComment(Comment comment){
        this.comment = comment;
    }
    public void addReviewer(Member member){
        reviewer = member;
    }

    public void update(String content, Double score) {
        if(content != null && !content.isEmpty()) {
            this.content = content;
        }
        if(!score.isNaN() && 0.0 <= score && score <= 5.0) {
            this.content = content;
        }
    }
}
