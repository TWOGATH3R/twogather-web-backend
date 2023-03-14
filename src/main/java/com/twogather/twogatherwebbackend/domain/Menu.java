package com.twogather.twogatherwebbackend.domain;


import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
public class Menu {
    @Id
    @GeneratedValue
    @Column(name="menu_id")
    private Long id;
    private String name;
    @ManyToOne
    private Store store;
    private Integer price;
    private String description;
}
