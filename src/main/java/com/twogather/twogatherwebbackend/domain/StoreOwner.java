package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class StoreOwner extends User{
    @Id
    @GeneratedValue
    @Column(name ="store_owner_id")
    private Long storeOwnerId;

    @OneToMany(mappedBy = "owner")
    private List<Store> storeList = new ArrayList<>();

    private String businessNumber;
    private String businessName;
    private LocalDate businessStartDate;

    public StoreOwner(String email, String loginPw, String name, String phone, String businessNumber, String businessName, LocalDate businessStartDate) {
        super(email, loginPw, name, phone);
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.businessStartDate = businessStartDate;
    }
}
