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

    public Review(Store store, Member reviewer, String content, Double score, LocalDate createdDate){
        this.store = store;
        this.reviewer = reviewer;
        this.content = content;
        this.score = score;
        this.createdDate = createdDate;
    }
    public void addComment(Comment comment){
        if(commentList==null){
            commentList = new ArrayList<>();
        }
        this.commentList.add(comment);
    }

}
