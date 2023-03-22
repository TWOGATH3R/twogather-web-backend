package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private String serverFileName;
}
