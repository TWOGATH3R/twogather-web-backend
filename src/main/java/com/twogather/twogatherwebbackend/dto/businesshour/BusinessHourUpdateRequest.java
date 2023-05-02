package com.twogather.twogatherwebbackend.dto.businesshour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class BusinessHourUpdateRequest implements StartTimeBeforeEndTime {
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Long storeId;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalTime startTime;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalTime endTime;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Boolean isOpen;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Boolean hasBreakTime;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalTime breakStartTime;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalTime breakEndTime;
}
