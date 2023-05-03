package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.dto.member.ConsumerSaveRequest;
import com.twogather.twogatherwebbackend.dto.member.LoginRequest;
import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TestConstants {
    public static final PasswordEncoder passwordEncoded = new BCryptPasswordEncoder();
    public static final Long STORE_ID = 1L;
    public static final Long INVALID_STORE_ID = -1L;
    public static final Long ANOTHER_STORE_ID = 2L;
    public static final String START_TIME_STRING = "11:30";
    public static final String END_TIME_STRING = "20:00";
    public static final String BREAK_START_TIME_STRING = "13:30";
    public static final String BREAK_END_TIME_STRING = "14:00";
    public static final LocalTime BREAK_START_TIME = LocalTime.parse(BREAK_START_TIME_STRING);
    public static final LocalTime BREAK_END_TIME = LocalTime.parse(BREAK_END_TIME_STRING);
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

    public static final String STORE_NAME = "김가네 닭갈비";
    public static final String STORE_ADDRESS = "전주시 평화동 어쩌고 222-2";
    public static final String STORE_PHONE = "063-231-2222";


    public static final String WRONG_PASSWORD = "1asd@@@SAdad@@";

    public static final String AUTH = "Authorization";

    public static final Long BUSINESS_HOUR_ID = 1l;

    public static final LoginRequest OWNER_LOGIN_REQUEST = new LoginRequest(OWNER_EMAIL, OWNER_PASSWORD);
    public static final LoginRequest OWNER_INVALID_LOGIN_REQUEST = new LoginRequest(OWNER_EMAIL, WRONG_PASSWORD);
    public static final ConsumerSaveRequest CONSUMER_SAVE_REQUEST = new ConsumerSaveRequest(CONSUMER_EMAIL, CONSUMER_PASSWORD, CONSUMER_NAME, CONSUMER_PHONE);
    public static final StoreOwnerSaveRequest OWNER_SAVE_REQUEST =
            new StoreOwnerSaveRequest(
                OWNER_EMAIL, OWNER_PASSWORD, OWNER_NAME, OWNER_PHONE,
                    OWNER_BUSINESS_NUMBER, OWNER_BUSINESS_NAME, OWNER_BUSINESS_START_DATE
            );

    public static final BusinessHourSaveRequest INVALID_BUSINESS_HOUR_SAVE_REQUEST =
            new BusinessHourSaveRequest(INVALID_STORE_ID, END_TIME, START_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

    public static final StoreSaveRequest STORE_SAVE_REQUEST =
            new StoreSaveRequest(STORE_NAME, STORE_ADDRESS, STORE_PHONE);
    public static final StoreOwner STORE_OWNER =
            new StoreOwner(OWNER_EMAIL, passwordEncoded.encode(OWNER_PASSWORD), OWNER_NAME, OWNER_PHONE,
                    OWNER_BUSINESS_NUMBER, OWNER_BUSINESS_NAME, LocalDate.parse(OWNER_BUSINESS_START_DATE,
                    DateTimeFormatter.ofPattern("yyyyMMdd")),
                    com.twogather.twogatherwebbackend.domain.AuthenticationType.OWNER, true);

    public static final BusinessHourSaveRequest BUSINESS_HOUR_SAVE_REQUEST =
            new BusinessHourSaveRequest(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

    public static final BusinessHourResponse BUSINESS_HOUR_SAVE_RESPONSE =
            new BusinessHourResponse(BUSINESS_HOUR_ID, STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

}
