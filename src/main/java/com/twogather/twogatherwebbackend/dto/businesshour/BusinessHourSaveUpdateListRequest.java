package com.twogather.twogatherwebbackend.dto.businesshour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BusinessHourSaveUpdateListRequest {
    @Valid
    private List<BusinessHourSaveUpdateInfo> businessHourList;
}