package com.twogather.twogatherwebbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="store_id")
    private Long storeId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private StoreOwner owner;

    @OneToMany(mappedBy = "store")
    private List<Image> storeImageList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<BusinessHour> businessHourList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Menu> menuList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String name;
    private String address;
    private String phone;


    public Store(StoreOwner owner, List<BusinessHour> businessHourList, List<Menu> menuList, String name, String address, String phone){
        this.owner = owner;
        this.businessHourList = businessHourList;
        this.menuList = menuList;
        this.name=name;
        this.address=address;
        this.phone=phone;
    }
    public Store(String name, String address, String phone){
        this.name=name;
        this.address=address;
        this.phone=phone;
    }
    public Store(Long id, String name, String address, String phone){
        this.storeId = id;
        this.name=name;
        this.address=address;
        this.phone=phone;
    }
    public void updateName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }
    public void updateAddress(String address) {
        if (address != null && !address.isEmpty()) {
            this.address = address;
        }
    }
    public void updatePhone(String phone) {
        if (phone != null && !phone.isEmpty()) {
            this.phone = phone;
        }
    }

}
