package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="keyword_id")
    private Long keywordId;
    @Column(unique = true, length = 20)
    private String name;

    public Keyword(String name){
        this.name = name;
    }

}
