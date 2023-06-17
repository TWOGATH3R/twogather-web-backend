package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "store_member_unique", columnNames = {"store_id", "member_id"})
})
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="likes_id")
    private Long likesId;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Likes(Store store, Member member){
        this.store =store;
        store.addLikes(this);
        this.member = member;
    }
}
