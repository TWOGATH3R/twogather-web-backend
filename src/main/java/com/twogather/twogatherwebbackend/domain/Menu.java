package com.twogather.twogatherwebbackend.domain;


import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="menu_id")
    private Long menuId;
    private String name;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    private Integer price;
    private String description;
}
