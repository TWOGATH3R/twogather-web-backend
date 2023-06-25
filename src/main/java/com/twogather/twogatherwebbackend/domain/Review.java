package com.twogather.twogatherwebbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member reviewer;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    @Column(name = "content", columnDefinition = "VARCHAR(5000)")
    private String content;
    private Double score;
    private LocalDate createdDate;

    public void addStore(Store store){
        this.store = store;
        this.store.addReview(this);
    }
    public void addComment(Comment comment){
        if(commentList==null){
            commentList = new ArrayList<>();
        }
        this.commentList.add(comment);
    }
    public void addReviewer(Member member){
        reviewer = member;
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
