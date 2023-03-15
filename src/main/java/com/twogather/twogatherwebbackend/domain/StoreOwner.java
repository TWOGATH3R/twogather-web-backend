package com.twogather.twogatherwebbackend.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class StoreOwner extends User{
    @Id
    @GeneratedValue
    @Column(name ="store_owner_id")
    private Long id;

    @OneToMany(mappedBy = "owner")
    private List<Store> storeList = new ArrayList<>();

    private String businessNumber;
    private String businessName;
    private LocalDate businessStartDate;
}
