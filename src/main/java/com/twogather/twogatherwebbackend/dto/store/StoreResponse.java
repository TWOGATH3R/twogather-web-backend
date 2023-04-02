package com.twogather.twogatherwebbackend.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StoreResponse {
    private Long id;
    private String name;
    private String address;
    private String phone;
}
