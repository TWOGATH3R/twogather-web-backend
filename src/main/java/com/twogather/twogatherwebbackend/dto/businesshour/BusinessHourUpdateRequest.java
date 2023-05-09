package com.twogather.twogatherwebbackend.dto.businesshour;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twogather.twogatherwebbackend.dto.valid.StartBeforeEnd;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEnd
public class BusinessHourUpdateRequest implements StartTimeBeforeEndTime {
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Long storeId;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Long businessHourId;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Boolean isOpen;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Boolean hasBreakTime;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime breakStartTime;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime breakEndTime;
}
