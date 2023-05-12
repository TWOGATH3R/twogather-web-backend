package com.twogather.twogatherwebbackend.dto.businesshour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class BusinessHourSaveRequest extends BusinessHourRequest {
    public BusinessHourSaveRequest(Long storeId, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, Boolean isOpen, Boolean hasBreakTime, LocalTime breakStartTime, LocalTime breakEndTime) {
        super(storeId, startTime, endTime, dayOfWeek, isOpen, hasBreakTime, breakStartTime, breakEndTime);
    }
}
