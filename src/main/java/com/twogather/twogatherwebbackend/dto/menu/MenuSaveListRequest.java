package com.twogather.twogatherwebbackend.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MenuSaveListRequest {
    @Valid
    private List<MenuSaveInfo> menuSaveList;
}