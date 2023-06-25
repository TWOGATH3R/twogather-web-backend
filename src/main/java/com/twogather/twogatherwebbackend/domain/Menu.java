package com.twogather.twogatherwebbackend.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="menu_id")
    private Long menuId;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
    private Integer price;

    public Menu(Store store, String name, Integer price){
        if (this.store != null) {
            this.store.getMenuList().remove(this);
        }
        this.store = store;
        store.addMenu(this);
        this.name = name;
        this.price = price;
    }
    public Menu update(String name, Integer price){
        if(!name.isEmpty()){
            this.name = name;
        }
        if(price!=null){
            this.price = price;
        }
        return this;
    }

}
