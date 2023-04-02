package com.twogather.twogatherwebbackend.dto.businesshour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessHourResponse {
    private Long businessHourId;
    private Long storeId;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
    private boolean isOpen;
}
