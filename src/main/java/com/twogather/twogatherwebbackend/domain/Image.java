package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private String url;

    public Image(Store store, String url){
        if (this.store != null) {
            this.store.getStoreImageList().remove(this); 
        }
        this.store = store;
        this.store.addImage(this);
        this.url = url;
    }
}
