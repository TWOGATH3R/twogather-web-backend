package com.twogather.twogatherwebbackend.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuIdList {
    List<Long> menuIdList = new ArrayList<>();
}
