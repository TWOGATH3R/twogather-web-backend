package com.twogather.twogatherwebbackend.dto.businesshour;

import java.time.LocalTime;

public interface StartTimeBeforeEndTime {
    LocalTime getStartTime();
    LocalTime getEndTime();
}
