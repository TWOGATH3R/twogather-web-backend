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
@DiscriminatorValue("store_owner")
public class StoreOwner extends Member {
    @OneToMany(mappedBy = "owner")
    private List<Store> storeList = new ArrayList<>();


    public StoreOwner(String username, String email, String loginPw, String name,AuthenticationType authenticationType, boolean isActive) {
        super(username, email, loginPw, name, authenticationType, isActive);

    }


}
