package com.twogather.twogatherwebbackend.dto.businesshour;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class BusinessHourSaveRequest implements StartTimeBeforeEndTime {
    @NotNull(message = "storeId는 필수 입력 항목입니다.")
    private Long storeId;

    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
    private boolean isOpen;
}
