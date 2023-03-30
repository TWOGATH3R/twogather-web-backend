package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.dto.member.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveRequest;

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

    public static final String OWNER_EMAIL = "asd@naver.com";
    public static final String OWNER_PASSWORD = "asd@asd@@123";
    public static final String OWNER_NAME = "루터";
    public static final String OWNER_PHONE = "01012341234";
    public static final String OWNER_BUSINESS_NAME = "루터";
    public static final String OWNER_BUSINESS_NUMBER = "0000000000";
    public static final String OWNER_BUSINESS_START_DATE = "20200101";

    public static final String CONSUMER_EMAIL = "consumer@naver.com";
    public static final String CONSUMER_PASSWORD = "asd!asd123";
    public static final String CONSUMER_NAME = "김소비";
    public static final String CONSUMER_PHONE = "01012341234";

    public static final String MEMBER_EMAIL = "pobi@email.com";
    public static final String MEMBER_PASSWORD = "test1234!!!";
    public static final String MEMBER_NAME = "루터";
    public static final String MEMBER_PHONE = "01012341234";

    public static final String WRONG_PASSWORD = "1asd@@@SAdad@@";

    public static final LoginRequest OWNER_LOGIN_REQUEST = new LoginRequest(OWNER_EMAIL, OWNER_PASSWORD);
    public static final LoginRequest OWNER_INVALID_LOGIN_REQUEST = new LoginRequest(OWNER_EMAIL, WRONG_PASSWORD);
    public static final ConsumerSaveRequest CONSUMER_SAVE_REQUEST = new ConsumerSaveRequest(CONSUMER_EMAIL, CONSUMER_PASSWORD, CONSUMER_NAME, CONSUMER_PHONE);
    public static final StoreOwnerSaveRequest OWNER_SAVE_REQUEST =
            new StoreOwnerSaveRequest(
                OWNER_EMAIL, OWNER_PASSWORD, OWNER_NAME, OWNER_PHONE,
                    OWNER_BUSINESS_NUMBER, OWNER_BUSINESS_NAME, OWNER_BUSINESS_START_DATE
            );



}
