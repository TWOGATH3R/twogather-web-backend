package com.twogather.twogatherwebbackend.dto.businesshour;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class BusinessHourSaveUpdateRequest {
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    protected Long storeId;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    protected LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    protected LocalTime endTime;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    protected DayOfWeek dayOfWeek;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    protected Boolean isOpen;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    protected Boolean hasBreakTime;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    protected LocalTime breakStartTime;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    protected LocalTime breakEndTime;

    public BusinessHourSaveUpdateRequest(Long storeId, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek,
                                         Boolean isOpen, Boolean hasBreakTime, LocalTime breakStartTime,
                                         LocalTime breakEndTime) {
        this.storeId = storeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.isOpen = isOpen;
        this.hasBreakTime = hasBreakTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
}
