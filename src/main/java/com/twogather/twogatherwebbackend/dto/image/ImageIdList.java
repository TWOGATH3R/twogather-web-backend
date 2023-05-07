package com.twogather.twogatherwebbackend.dto.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageIdList {
    List<Long> imageIdList = new ArrayList<>();
}
