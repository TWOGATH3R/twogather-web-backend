package com.twogather.twogatherwebbackend.dto.businesshour;

import com.twogather.twogatherwebbackend.valid.StartBeforeEnd;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@StartBeforeEnd
public class BusinessHourSaveRequest implements StartTimeBeforeEndTime {
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Long storeId;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalTime startTime;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalTime endTime;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private boolean isOpen;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private boolean hasBreakTime;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalTime breakStartTime;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalTime breakEndTime;


}
