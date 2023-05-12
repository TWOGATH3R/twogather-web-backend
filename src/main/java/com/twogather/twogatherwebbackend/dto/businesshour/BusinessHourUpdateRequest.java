package com.twogather.twogatherwebbackend.dto.businesshour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class BusinessHourUpdateRequest extends BusinessHourRequest  {

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Long businessHourId;
    public BusinessHourUpdateRequest(Long storeId, Long businessHourId, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, Boolean isOpen, Boolean hasBreakTime, LocalTime breakStartTime, LocalTime breakEndTime) {
        super(storeId, startTime, endTime, dayOfWeek, isOpen, hasBreakTime, breakStartTime, breakEndTime);
        this.businessHourId = businessHourId;
    }
}
