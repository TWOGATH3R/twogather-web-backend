package com.twogather.twogatherwebbackend;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourIdList;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourUpdateRequest;
import com.twogather.twogatherwebbackend.dto.image.ImageIdList;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.dto.member.*;
import com.twogather.twogatherwebbackend.dto.review.MyReviewInfoResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewSaveRequest;
import com.twogather.twogatherwebbackend.dto.review.ReviewUpdateRequest;
import com.twogather.twogatherwebbackend.dto.store.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    public static final LocalDate OWNER_BUSINESS_START_DATE = LocalDate.now();

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
    public static final Member MEMBER =
            new Member(1l, MEMBER_EMAIL, MEMBER_PASSWORD, MEMBER_NAME, AuthenticationType.OWNER,true);

    public static final ConsumerResponse CONSUMER_RESPONSE
            = new ConsumerResponse(1l, "김멍치","sda@naer.com");
    public static final LoginRequest OWNER_LOGIN_REQUEST = new LoginRequest(OWNER_EMAIL, OWNER_PASSWORD);
    public static final LoginRequest OWNER_INVALID_LOGIN_REQUEST = new LoginRequest(OWNER_EMAIL, WRONG_PASSWORD);
    public static final ConsumerSaveUpdateRequest CONSUMER_SAVE_UPDATE_REQUEST = new ConsumerSaveUpdateRequest(CONSUMER_EMAIL, CONSUMER_PASSWORD, CONSUMER_NAME);
    public static final StoreOwnerSaveUpdateRequest OWNER_SAVE_REQUEST =
            new StoreOwnerSaveUpdateRequest(
                    OWNER_EMAIL, OWNER_PASSWORD, OWNER_NAME,
                    OWNER_BUSINESS_NUMBER, OWNER_BUSINESS_NAME, OWNER_BUSINESS_START_DATE
            );

    public static final BusinessHourSaveRequest INVALID_BUSINESS_HOUR_SAVE_REQUEST =
            new BusinessHourSaveRequest(INVALID_STORE_ID, END_TIME, START_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

    public static final StoreSaveRequest STORE_SAVE_REQUEST =
            new StoreSaveRequest(STORE_NAME, STORE_ADDRESS, STORE_PHONE);
    public static final StoreOwner STORE_OWNER =
            new StoreOwner(OWNER_EMAIL, passwordEncoded.encode(OWNER_PASSWORD), OWNER_NAME,
                    OWNER_BUSINESS_NUMBER, OWNER_BUSINESS_NAME, OWNER_BUSINESS_START_DATE,
                    com.twogather.twogatherwebbackend.domain.AuthenticationType.OWNER, true);

    public static final BusinessHourSaveRequest BUSINESS_HOUR_SAVE_REQUEST =
            new BusinessHourSaveRequest(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

    public static final BusinessHourUpdateRequest BUSINESS_HOUR_UPDATE_REQUEST =
            new BusinessHourUpdateRequest(STORE_ID, BUSINESS_HOUR_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

    public static final BusinessHourResponse BUSINESS_HOUR_RESPONSE =
            new BusinessHourResponse(BUSINESS_HOUR_ID, STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

    public static final StoreResponse STORE_RESPONSE =
            new StoreResponse(1l, "가게이름", "전주시 평화동 산동 2길 1-3","010-1234-1234");
    public static final StoresResponse STORES_RESPONSE =
            new StoresResponse(1l, "가게이름", "전주시 평화동 산동 2길 1-3",4.2);
    public static final MyStoreResponse MY_STORES_RESPONSE =
            new MyStoreResponse(1l, "가게이름", "전주시 평화동 산동 2길 1-3",false, "자격미달");

    public static final StoreSaveRequest STORE_REQUEST =
            new StoreSaveRequest("가게이름", "전주시 평화동 산동 2길 1-3","010-1234-1234");
    public static final StoreUpdateRequest STORE_UPDATE_REQUEST =
            new StoreUpdateRequest("가게이름", "전주시 평화동 산동 2길 1-3","010-1234-1234");

    public static final StoreOwnerSaveUpdateRequest STORE_OWNER_REQUEST =
            new StoreOwnerSaveUpdateRequest("sad@baer.co", "p23dasdaw","사업자이름", "0000000000", "이름",LocalDate.now());
    public static final StoreOwnerResponse STORE_OWNER_RESPONSE =
            new StoreOwnerResponse(1l, "사업자이름", "ifd@naebr.com", "0000000000", "비즈니스이름", LocalDate.now());
    public static final String DATE = "2020-02-02";
    public static final Page<MyReviewInfoResponse> MY_REVIEW_LIST = new PageImpl<>(List.of(
            new MyReviewInfoResponse(1L,  "Good product", 4.5, LocalDate.of(2022, 1, 1), "imageurl", "김가네삼겹살", "서울시 어쩌고 어쩌고", "김은지"),
            new MyReviewInfoResponse(1L, "Not bad", 3.0, LocalDate.of(2022, 1, 3), "imageurl2", "레스토랑1", "경기도 어쩌고 어쩌고", "박은지"),
            new MyReviewInfoResponse(2L, "Excellent", 5.0, LocalDate.of(2022, 1, 5), "imageurl3", "김밥집1", "전주시 어쩌고 어쩌고", "김지은")
    ));
    public static final ReviewResponse REVIEW_RESPONSE =
            new ReviewResponse(1L, 1L,"너무 맛있어요~!" , 3.2, LocalDate.parse(DATE));
    public static final ReviewSaveRequest REVIEW_SAVE_REQUEST =
            new ReviewSaveRequest(1l,1l,"진짜맛있어요!", 1.2);
    public static final ReviewUpdateRequest REVIEW_UPDATE_REQUEST =
            new ReviewUpdateRequest(1l,1l,"진짜맛있어요!", 1.2);
    public static final MockMultipartFile IMAGE1
            = new MockMultipartFile("images", "image1.jpg", "image/jpeg", "test data".getBytes());
    public static final MockMultipartFile IMAGE2
            = new MockMultipartFile("imagesSDA", "imageS2.jpg", "image/jpeg", "test data".getBytes());

    public static final ImageIdList IMAGE_ID_LIST =
            new ImageIdList(
                    new ArrayList<>(){{
                        add(1l);
                        add(2l);
                        add(3l);
                    }});
    public static final ImageResponse IMAGE_RESPONSE =
            new ImageResponse(1l, "www.maver/ssd/c");
    public static final ArrayList IMAGE_RESPONSE_LIST =
            new ArrayList<ImageResponse>(){{
                add(IMAGE_RESPONSE);
                add(IMAGE_RESPONSE);
                add(IMAGE_RESPONSE);
            }};
    public static final ArrayList STORES_RESPONSE_LIST =
            new ArrayList<StoresResponse>(){{
                add(STORES_RESPONSE);
                add(STORES_RESPONSE);
                add(STORES_RESPONSE);
            }};
    public static final ArrayList MY_STORES_RESPONSE_LIST =
            new ArrayList<MyStoreResponse>(){{
                add(MY_STORES_RESPONSE);
                add(MY_STORES_RESPONSE);
                add(MY_STORES_RESPONSE);
            }};
    public static final ArrayList BUSINESS_HOUR_RESPONSE_LIST =
            new ArrayList<BusinessHourResponse>(){{
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
            }};
    public static final ArrayList BUSINESS_HOUR_UPDATE_REQUEST_LIST =
            new ArrayList<BusinessHourUpdateRequest>(){{
                add(BUSINESS_HOUR_UPDATE_REQUEST);
                add(BUSINESS_HOUR_UPDATE_REQUEST);
            }};
    public static final ArrayList BUSINESS_HOUR_SAVE_REQUEST_LIST =
            new ArrayList<BusinessHourSaveRequest>(){{
                add(BUSINESS_HOUR_SAVE_REQUEST);
                add(BUSINESS_HOUR_SAVE_REQUEST);
                add(BUSINESS_HOUR_SAVE_REQUEST);
            }};
    public static final ArrayList BUSINESS_HOUR_RESPONSE_7_LIST =
            new ArrayList<BusinessHourResponse>(){{
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
            }};
    public static final ArrayList ID_LIST =
            new ArrayList<Long>(){
                {
                    add(1l);
                    add(2l);
                    add(3l);
                }
            };
    public static final BusinessHourIdList BUSINESS_HOUR_ID_LIST =
            new BusinessHourIdList(ID_LIST);
}
