package com.twogather.twogatherwebbackend.dto.businesshour;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessHourResponse {
    private Long businessHourId;
    private Long storeId;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
    private Boolean isOpen;
    private Boolean hasBreakTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakStartTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakEndTime;

}
