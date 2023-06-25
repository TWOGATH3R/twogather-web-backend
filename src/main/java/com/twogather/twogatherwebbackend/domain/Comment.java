package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_id")
    private Long commentId;
    @Column(name = "content", columnDefinition = "VARCHAR(5000)")
    private String content;
    private LocalDate createdDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member commenter;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;


    public Comment(String content, Review review, Member member){
        this.review = review;
        this.review.addComment(this);
        this.commenter = member;
        commenter.addComment(this);
        this.content = content;
        this.createdDate = LocalDate.now();
        review.addComment(this);
    }
    public void update(String content){
        if(!content.isEmpty()){
            this.content = content;
        }
    }
}
