package com.twogather.twogatherwebbackend.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="store_id")
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private StoreOwner owner;

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<Image> storeImageList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<BusinessHour> businessHourList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<Menu> menuList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<Review> reviewList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    private List<StoreKeyword> storeKeywordList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
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
    private String businessNumber;
    private String businessName;
    private LocalDate businessStartDate;

    public Store(StoreOwner owner, String name, String address, String phone, String businessName, String businessNumber, LocalDate businessStartDate){
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.owner = owner;
        this.status = StoreStatus.PENDING;
        this.reasonForRejection = "";
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.businessStartDate = businessStartDate;
    }
    public void update(String storeName, String address, String phone, String businessName, String businessNumber, LocalDate businessStartDate) {
        if (storeName != null && !storeName.isEmpty()) {
            this.name = storeName;
        }
        if (address != null && !address.isEmpty()) {
            this.address = address;
        }
        if (phone != null && !phone.isEmpty()) {
            this.phone = phone;
        }
        if (businessName != null && !businessName.isEmpty()) {
            this.businessName = businessName;
        }
        if (businessNumber != null && !businessNumber.isEmpty()) {
            this.businessNumber = businessNumber;
        }
        if (businessStartDate != null) {
            this.businessStartDate = businessStartDate;
        }
    }
    public void setCategory(Category category){
        if(category!=null){
            this.category = category;
        }
    }
    public void clearKeyword(){
        this.storeKeywordList = new ArrayList<>();
    }
    public void delete(){
        this.status = StoreStatus.DELETED;
    }
    public void approve(){
        this.status = StoreStatus.APPROVED;
    }

}
