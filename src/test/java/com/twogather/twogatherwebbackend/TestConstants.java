package com.twogather.twogatherwebbackend;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class TestConstants {
    public static final Long STORE_ID = 1L;
    public static final Long ANOTHER_STORE_ID = 2L;
    public static final String START_TIME_STRING = "11:30";
    public static final String END_TIME_STRING = "20:00";
    public static final LocalTime START_TIME = LocalTime.parse(START_TIME_STRING);
    public static final LocalTime END_TIME = LocalTime.parse(END_TIME_STRING);
    public static final Integer DAY_OF_WEEK_INTEGER = 1;
    public static final DayOfWeek DAY_OF_WEEK = DayOfWeek.of(DAY_OF_WEEK_INTEGER);
    public static final boolean IS_OPEN = true;
}
