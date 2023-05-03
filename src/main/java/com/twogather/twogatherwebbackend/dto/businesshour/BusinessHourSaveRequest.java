package com.twogather.twogatherwebbackend.dto.businesshour;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class BusinessHourSaveRequest implements StartTimeBeforeEndTime {
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Long storeId;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "비어있는 항목을 입력해주세요.")//open으로 받아짐
    private Boolean isOpen;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Boolean hasBreakTime;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakStartTime;

    @DateTimeFormat(pattern = "HH:mm")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakEndTime;


}
