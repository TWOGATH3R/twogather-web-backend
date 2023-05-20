package com.twogather.twogatherwebbackend.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuUpdateRequest {
    @NotNull
    private Long menuId;
    @NotBlank
    private String name;
    @NotNull
    private Integer price;
}
