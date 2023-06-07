package com.twogather.twogatherwebbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private StoreOwner owner;

    @OneToMany(mappedBy = "store")
    private List<Image> storeImageList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<BusinessHour> businessHourList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<StoreKeyword> storeKeywordList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Likes> likesList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;
    private String address;
    private String phone;
    @Enumerated(EnumType.STRING)
    private StoreStatus status;
    private String reasonForRejection;
    private LocalDate requestDate = LocalDate.now();


    public Store(StoreOwner owner, List<BusinessHour> businessHourList, List<Menu> menuList, String name, String address, String phone, StoreStatus status, String reasonForRejection){
        this.owner = owner;
        this.businessHourList = businessHourList;
        this.menuList = menuList;
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.status = status;
        this.reasonForRejection = reasonForRejection;
    }
    public Store(Category category, StoreOwner owner, List<BusinessHour> businessHourList, List<Menu> menuList, String name, String address, String phone, StoreStatus status, String reasonForRejection){
        this.category = category;
        this.owner = owner;
        this.businessHourList = businessHourList;
        this.menuList = menuList;
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.status = status;
        this.reasonForRejection = reasonForRejection;
    }
    public Store(String name, String address, String phone){
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.status = StoreStatus.PENDING;
        this.reasonForRejection = "";
    }
    public Store(StoreOwner owner, String name, String address, String phone){
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.owner = owner;
        this.status = StoreStatus.PENDING;
        this.reasonForRejection = "";
    }
    public Store(Long id, String name, String address, String phone, StoreStatus status, String reasonForRejection){
        this.storeId = id;
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.status = status;
        this.reasonForRejection = reasonForRejection;
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
    public void setCategory(Category category){
        if(category!=null){
            this.category = category;
        }
    }
    public void reject(String reasonForRejection){
        this.status = StoreStatus.DENIED;
        this.reasonForRejection = reasonForRejection;
    }
    public void delete(){
        this.status = StoreStatus.DELETED;
    }
    public void approve(){
        this.status = StoreStatus.APPROVED;
    }

}
