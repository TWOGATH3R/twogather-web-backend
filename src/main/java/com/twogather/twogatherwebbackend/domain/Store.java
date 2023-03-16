package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Store {
    @Id
    @GeneratedValue
    @Column(name ="store_id")
    private Long storeId;

    @ManyToOne
    private StoreOwner owner;

    @OneToMany(mappedBy = "store")
    private List<Image> storeImageList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<BusinessHour> businessHourList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Menu> menuList = new ArrayList<>();

    @OneToOne
    private Category category;


    private String name;
    private String address;
    private String phone;

}
