package com.twogather.twogatherwebbackend.dto.businesshour;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twogather.twogatherwebbackend.dto.valid.BusinessHourValidation;
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
@BusinessHourValidation
public abstract class BusinessHourRequest {
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
}
