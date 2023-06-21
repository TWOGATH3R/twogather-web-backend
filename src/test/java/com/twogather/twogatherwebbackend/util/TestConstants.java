package com.twogather.twogatherwebbackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourIdList;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateInfo;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import com.twogather.twogatherwebbackend.dto.category.CategoryResponse;
import com.twogather.twogatherwebbackend.dto.comment.CommentResponse;
import com.twogather.twogatherwebbackend.dto.comment.CommentSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.email.EmailRequest;
import com.twogather.twogatherwebbackend.dto.email.VerificationCodeResponse;
import com.twogather.twogatherwebbackend.dto.image.ImageIdList;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.dto.member.*;
import com.twogather.twogatherwebbackend.dto.menu.*;
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
import java.util.Arrays;
import java.util.List;

public class TestConstants {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Store Constants
    public static final Long STORE_ID = 1L;
    public static final Long INVALID_STORE_ID = -1L;
    public static final String STORE_NAME = "김가네 닭갈비";
    public static final String STORE_ADDRESS = "전주시 평화동 어쩌고 222-2";
    public static final String STORE_PHONE = "063-231-2222";
    public static final Store APPROVED_STORE = new Store(1l, null,null,null,null,null, null,null,null,"이름", "주소","010-1234-1234", StoreStatus.APPROVED,"",LocalDate.now(), "0000000000", "홍길동", LocalDate.now());
    // Business Hour Constants
    public static final String START_TIME_STRING = "11:30";
    public static final String END_TIME_STRING = "20:00";
    public static final String BREAK_START_TIME_STRING = "13:30";
    public static final String BREAK_END_TIME_STRING = "14:00";
    public static final LocalTime BREAK_START_TIME = LocalTime.parse(BREAK_START_TIME_STRING);
    public static final LocalTime BREAK_END_TIME = LocalTime.parse(BREAK_END_TIME_STRING);
    public static final LocalTime START_TIME = LocalTime.parse(START_TIME_STRING);
    public static final LocalTime END_TIME = LocalTime.parse(END_TIME_STRING);
    public static final DayOfWeek DAY_OF_WEEK = DayOfWeek.MONDAY;
    public static final boolean IS_OPEN = true;

    // Owner Constants
    public static final String OWNER_USERNAME = "owner1";
    public static final String OWNER_EMAIL = "asd@naver.com";
    public static final String OWNER_PASSWORD = "asdqwea123";
    public static final String OWNER_NAME = "루터";
    public static final MemberSaveRequest OWNER_SAVE_REQUEST = new MemberSaveRequest(
            OWNER_EMAIL, OWNER_USERNAME, OWNER_PASSWORD, OWNER_NAME
    );

    //admin
    public static final String ADMIN_EMAIL = "admin@naber.com";
    public static final String ADMIN_USERNAME = "admin112";
    public static final String ADMIN_PASSWORD = "aasdasd123";
    public static final Consumer ADMIN = new Consumer(ADMIN_USERNAME, ADMIN_EMAIL, passwordEncoder.encode(ADMIN_PASSWORD), "김관리자", AuthenticationType.ADMIN, true);

    // Consumer Constants
    public static final String CONSUMER_USERNAME = "consumer1";
    public static final String CONSUMER_EMAIL = "consumer@naver.com";
    public static final String CONSUMER_PASSWORD = "asdasd123";
    public static final String CONSUMER_NAME = "김소비";
    public static final MemberSaveRequest CONSUMER_SAVE_REQUEST =
            new MemberSaveRequest(CONSUMER_EMAIL, CONSUMER_USERNAME, CONSUMER_PASSWORD, CONSUMER_NAME);
    public static final Consumer CONSUMER = new Consumer(CONSUMER_USERNAME, CONSUMER_EMAIL,
            passwordEncoder.encode(CONSUMER_PASSWORD), CONSUMER_NAME, AuthenticationType.CONSUMER, true);

    // Member Constants
    public static final String MEMBER_EMAIL = "pobi@email.com";
    public static final String MEMBER_PASSWORD = "testasd1234";
    public static final String MEMBER_NAME = "루터";

    // Common Constants
    public static final String WRONG_PASSWORD = "1asd@@@SAdad@@";
    public static final String AUTH = "Authorization";

    // Member Constants
    public static final String MEMBER_USERNAME = "member1";
    public static final Member MEMBER =
            new Member(1l, MEMBER_USERNAME, MEMBER_EMAIL, MEMBER_PASSWORD, MEMBER_NAME, AuthenticationType.STORE_OWNER, true);

    // Store Response with Keyword
    public static final ArrayList<String> KEYWORD_LIST = new ArrayList<>(Arrays.asList(
            "분위기좋은", "사진찍기좋은", "저렴한"
    ));
    public static final List<Long> KEYWORD_ID_LIST = new ArrayList<>(Arrays.asList(
            1l,2l,3l
    ));
    // Login Requests
    public static final LoginRequest OWNER_LOGIN_REQUEST = new LoginRequest( OWNER_USERNAME, OWNER_PASSWORD);
    public static final LoginRequest OWNER_INVALID_LOGIN_REQUEST = new LoginRequest(OWNER_USERNAME, WRONG_PASSWORD);
    public static final LoginRequest CONSUMER_LOGIN_REQUEST = new LoginRequest( CONSUMER_USERNAME, CONSUMER_PASSWORD);
    public static final LoginRequest ADMIN_LOGIN_REQUEST = new LoginRequest( ADMIN_USERNAME, ADMIN_PASSWORD);

    // Business Hour Save/Update Request
      public static final BusinessHourSaveUpdateInfo BUSINESS_HOUR_SAVE_UPDATE_INFO =
            new BusinessHourSaveUpdateInfo( START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null, null);

    // Business Hour Response
    public static final Long BUSINESS_HOUR_ID = 1l;
    public static final BusinessHourResponse BUSINESS_HOUR_RESPONSE =
            new BusinessHourResponse(BUSINESS_HOUR_ID, STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null, null);

    // Store Save/Update Request
    public static final StoreSaveUpdateRequest STORE_SAVE_REQUEST =
            new StoreSaveUpdateRequest(STORE_NAME, STORE_ADDRESS, STORE_PHONE, "0000000000", "홍길동", LocalDate.now(),  KEYWORD_ID_LIST,1L);
    public static final StoreSaveUpdateRequest STORE_SAVE_REQUEST2 =
            new StoreSaveUpdateRequest("other store", STORE_ADDRESS, STORE_PHONE, "0000000000", "홍길동", LocalDate.now(), KEYWORD_ID_LIST, 1L);

    public static final StoreOwner STORE_OWNER =
            new StoreOwner(OWNER_USERNAME, OWNER_EMAIL, passwordEncoder.encode(OWNER_PASSWORD), OWNER_NAME,
                    AuthenticationType.STORE_OWNER, true);
    public static final StoreSaveUpdateResponse STORE_SAVE_UPDATE_RESPONSE =
            new StoreSaveUpdateResponse(1l, "가게이름", "전주시 평화동 산동 2길 1-3", "010-1234-1234", "000000000","김길공",LocalDate.now(),KEYWORD_LIST, 1L);

    //default store response
    public static final StoreDefaultResponse STORE_DEFAULT_RESPONSE =
            StoreDefaultResponse
                    .builder()
                    .storeId(1L)
                    .storeName(STORE_NAME)
                    .categoryName("양식")
                    .keywordList(KEYWORD_LIST)
                    .address(STORE_ADDRESS)
                    .likeCount(2)
                    .phone(STORE_PHONE)
                    .build();
    // My Store Response
    public static final LocalDate DATE = LocalDate.parse("2020-02-02");
    public static final MyStoreResponse MY_STORES_RESPONSE =
            new MyStoreResponse(1l, "가게이름", "전주시 평화동 산동 2길 1-3", "063-231-4990", false, "자격미달", DATE, "url1");

    // Review Constants
    public static final Page<MyReviewInfoResponse> MY_REVIEW_LIST = new PageImpl<>(List.of(
            new MyReviewInfoResponse(1L, "Good product", 4.5, LocalDate.of(2022, 1, 1), "imageurl", "김가네삼겹살", "서울시 어쩌고 어쩌고", "김은지"),
            new MyReviewInfoResponse(1L, "Not bad", 3.0, LocalDate.of(2022, 1, 3), "imageurl2", "레스토랑1", "경기도 어쩌고 어쩌고", "박은지"),
            new MyReviewInfoResponse(2L, "Excellent", 5.0, LocalDate.of(2022, 1, 5), "imageurl3", "김밥집1", "전주시 어쩌고 어쩌고", "김지은")
    ));
    public static final ReviewResponse REVIEW_RESPONSE =
            new ReviewResponse(1L, 1L, "너무 맛있어요~!", 3.2, DATE);
    public static final ReviewSaveRequest REVIEW_SAVE_REQUEST =
            new ReviewSaveRequest(1l, 1l, "진짜맛있어요!", 1.2);
    public static final ReviewUpdateRequest REVIEW_UPDATE_REQUEST =
            new ReviewUpdateRequest(1l, 1l, "진짜맛있어요!", 1.2);


    // Owner Login Request
    public static final LoginRequest LOGIN_REQUEST =
            new LoginRequest( OWNER_USERNAME, OWNER_PASSWORD);

    // Image Id List
    public static final ImageIdList IMAGE_ID_LIST =
            new ImageIdList(Arrays.asList(1l, 2l, 3l));

    // Comment Constants
    public static final CommentSaveUpdateRequest COMMENT_SAVE_UPDATE_REQUEST =
            new CommentSaveUpdateRequest("내용내뇽ㅇ");
    public static final CommentResponse COMMENT_RESPONSE =
            new CommentResponse(1l, "대댓글내용", false, LocalDate.now());

    // Image Response
    public static final ImageResponse IMAGE_RESPONSE =
            new ImageResponse(1l, "www.maver/ssd/c");
    public static final List IMAGE_RESPONSE_LIST =
            new ArrayList<ImageResponse>(){{
                add(IMAGE_RESPONSE);
                add(IMAGE_RESPONSE);
                add(IMAGE_RESPONSE);
            }};

    // Email Constants
    public static final String VALID_EMAIL = "firefly_0@naver.com";
    public static final String INVALID_EMAIL = "firefASDly_0@naver.com";
    public static final VerificationCodeResponse VERIFICATION_CODE_RESPONSE =
            new VerificationCodeResponse("asVVaa");
    public static final EmailRequest EMAIL_REQUEST =
            new EmailRequest(VALID_EMAIL);

    // Category Constants
    public static final ArrayList<CategoryResponse> CATEGORY_RESPONSE_LIST = new ArrayList<>(Arrays.asList(
            new CategoryResponse(1l, "양식"),
            new CategoryResponse(2l, "일식"),
            new CategoryResponse(3l, "분식")
    ));
    public static final CategoryResponse CATEGORY_RESPONSE =
            new CategoryResponse(1l, "양식");

    public static final StoreResponseWithKeyword STORES_RESPONSE =
            new StoreResponseWithKeyword(1l, "가게이름", "전주시 평화동 산동 2길 1-3", 4.2, KEYWORD_LIST, "imageurl1", 12);
    public static final ArrayList STORES_RESPONSE_LIST =
            new ArrayList<StoreResponseWithKeyword>(){{
                add(STORES_RESPONSE);
                add(STORES_RESPONSE);
                add(STORES_RESPONSE);
            }};

    // Store Save/Update Request
    public static final StoreSaveUpdateRequest STORE_REQUEST =
            new StoreSaveUpdateRequest("가게이름", "전주시 평화동 산동 2길 1-3", "010-1234-1234", "0000000000", "홍길동", LocalDate.now(), KEYWORD_ID_LIST, 1L);
    public static final StoreSaveUpdateRequest STORE_UPDATE_REQUEST =
            new StoreSaveUpdateRequest("가게이름", "전주시 평화동 산동 2길 1-3", "010-1234-1234", "0000000000", "홍길동", LocalDate.now(), KEYWORD_ID_LIST, 1L);

    // My Store Response Page
    public static final Page<MyStoreResponse> MY_STORES_RESPONSE_PAGE =
            new PageImpl<>(List.of(MY_STORES_RESPONSE, MY_STORES_RESPONSE, MY_STORES_RESPONSE));

    // Top Store Response
    public static final List<TopStoreResponse> STORES_TOP3_TOP_RATED_RESPONSE = Arrays.asList(
            new TopStoreResponse(1l, "store1", 4.5, "123 Main St.", "url1", 23),
            new TopStoreResponse(1l, "store2", 4.2, "456 Oak Ave.", "url2", 12),
            new TopStoreResponse(1l, "store3", 4.0, "789 Elm St.", "url3", 12)
    );
    public static final List<TopStoreResponse> STORES_TOP10_RESPONSE_LIST = Arrays.asList(
            new TopStoreResponse(1l, "store1", 4.5, "123 Main St.", "url1",1),
            new TopStoreResponse(2l, "store2", 4.2, "456 Oak Ave.", "url2",2),
            new TopStoreResponse(3l, "store3", 3.0, "789 Elm St.", "url3",3),
            new TopStoreResponse(4l, "store4", 3.5, "123 Main St.", "url1",4),
            new TopStoreResponse(5l, "store5", 3.2, "456 Oak Ave.", "url2",5),
            new TopStoreResponse(6l, "store6", 3.0, "789 Elm St.", "url3",6),
            new TopStoreResponse(7l, "store7", 3.5, "123 Main St.", "url1",7),
            new TopStoreResponse(8l, "store8", 2.2, "456 Oak Ave.", "url2",12),
            new TopStoreResponse(9l, "store9", 1.0, "789 Elm St.", "url3",12)
    );

    // Menu Constants
    public static final ArrayList<MenuResponse> MENU_RESPONSE_LIST = new ArrayList<>(Arrays.asList(
            new MenuResponse(1l, "감자", 1000),
            new MenuResponse(2l, "케찹", 2000)
    ));
    public static final ArrayList<MenuSaveInfo> MENU_SAVE_LIST = new ArrayList<>(Arrays.asList(
            new MenuSaveInfo("감자", 1000),
            new MenuSaveInfo("케찹", 2000)
    ));
    public static final ArrayList<MenuSaveInfo> MENU_SAVE_NULL_LIST = new ArrayList<>(Arrays.asList(
            new MenuSaveInfo("감자", 1000),
            new MenuSaveInfo(null, 2000)
    ));
    public static final ArrayList<MenuSaveInfo> MENU_SAVE_MINUS_VALUE_LIST = new ArrayList<>(Arrays.asList(
            new MenuSaveInfo("감자", -1000)
    ));
    public static final ArrayList<MenuUpdateInfo> MENU_UPDATE_NULL_LIST = new ArrayList<>(Arrays.asList(
            new MenuUpdateInfo(1l,"감자", 1000),
            new MenuUpdateInfo(2l,null, 2000)
    ));
    public static final ArrayList<MenuUpdateInfo> MENU_UPDATE_MINUS_VALUE_LIST = new ArrayList<>(Arrays.asList(
            new MenuUpdateInfo(1l,"감자", -1000)
    ));
    public static final MenuSaveListRequest MENU_SAVE_LIST_REQUEST =
            new MenuSaveListRequest(MENU_SAVE_LIST);
    public static final MenuSaveListRequest MENU_SAVE_LIST_NULL_REQUEST =
            new MenuSaveListRequest(MENU_SAVE_NULL_LIST);
    public static final MenuUpdateListRequest MENU_UPDATE_LIST_NULL_REQUEST =
            new MenuUpdateListRequest(MENU_UPDATE_NULL_LIST);
    public static final MenuSaveListRequest MENU_SAVE_LIST_MINUS_VALUE_REQUEST =
            new MenuSaveListRequest(MENU_SAVE_MINUS_VALUE_LIST);
    public static final MenuUpdateListRequest MENU_UPDATE_LIST_MINUS_VALUE_REQUEST =
            new MenuUpdateListRequest(MENU_UPDATE_MINUS_VALUE_LIST);
    public static final ArrayList<MenuUpdateInfo> MENU_UPDATE_LIST = new ArrayList<>(Arrays.asList(
            new MenuUpdateInfo(1l, "new 감자", 10000),
            new MenuUpdateInfo(2l, "new 케찹", 20000)
    ));
    public static final MenuUpdateListRequest MENU_UPDATE_LIST_REQUEST =
            new MenuUpdateListRequest(MENU_UPDATE_LIST);

    // Business Hour Constants
    public static final ArrayList<BusinessHourResponse> BUSINESS_HOUR_RESPONSE_LIST =
            new ArrayList<>(Arrays.asList(BUSINESS_HOUR_RESPONSE, BUSINESS_HOUR_RESPONSE, BUSINESS_HOUR_RESPONSE));
    public static final ArrayList<BusinessHourSaveUpdateInfo> BUSINESS_HOUR_SAVE_UPDATE_LIST =
            new ArrayList<>(Arrays.asList(BUSINESS_HOUR_SAVE_UPDATE_INFO));
    public static final BusinessHourSaveUpdateListRequest BUSINESS_HOUR_SAVE_UPDATE_REQUEST_LIST =
            new BusinessHourSaveUpdateListRequest(BUSINESS_HOUR_SAVE_UPDATE_LIST);
    public static final ArrayList<BusinessHourResponse> BUSINESS_HOUR_RESPONSE_7_LIST =
            new ArrayList<>(Arrays.asList(BUSINESS_HOUR_RESPONSE, BUSINESS_HOUR_RESPONSE, BUSINESS_HOUR_RESPONSE,
                    BUSINESS_HOUR_RESPONSE, BUSINESS_HOUR_RESPONSE, BUSINESS_HOUR_RESPONSE, BUSINESS_HOUR_RESPONSE));
    public static final ArrayList<Long> ID_LIST =
            new ArrayList<>(Arrays.asList(1l, 2l, 3l));
    public static final BusinessHourIdList BUSINESS_HOUR_ID_LIST =
            new BusinessHourIdList(ID_LIST);

    //API
    public static final String OWNER_URL = "/api/owners";
    public static final String CONSUMER_URL = "/api/consumers";
    public static final String MEMBER_URL = "/api/members";
    public static final String STORE_URL = "/api/stores";
    public static final String CATEGORY_URL = "/api/categories";

    public TestConstants() throws JsonProcessingException {
    }

    //BusinessHour List
    public static BusinessHourSaveUpdateListRequest createBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateInfo businessHour1 = new BusinessHourSaveUpdateInfo(
                java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateInfo businessHour2 = new BusinessHourSaveUpdateInfo(
                java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.THURSDAY, true,
                false, null,null
        );
        ArrayList<BusinessHourSaveUpdateInfo> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        businessHourList.add(businessHour2);
        BusinessHourSaveUpdateListRequest request = new BusinessHourSaveUpdateListRequest(businessHourList);

        return request;
    }

    public static BusinessHourSaveUpdateListRequest createBusinessHourRequestWithAllDayOfWeek(Long storeId){
        List<BusinessHourSaveUpdateInfo> list = new ArrayList<>();
        BusinessHourSaveUpdateInfo businessHour1 = new BusinessHourSaveUpdateInfo(
                java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateInfo businessHour2 = new BusinessHourSaveUpdateInfo(
               java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.TUESDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateInfo businessHour3 = new BusinessHourSaveUpdateInfo(
                java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.WEDNESDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateInfo businessHour4 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.THURSDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateInfo businessHour5 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.SATURDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateInfo businessHour6 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.FRIDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateInfo businessHour7 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.SUNDAY, true,
                false, null,null
        );
        list.add(businessHour1);
        list.add(businessHour2);
        list.add(businessHour3);
        list.add(businessHour4);
        list.add(businessHour5);
        list.add(businessHour6);
        list.add(businessHour7);

        return new BusinessHourSaveUpdateListRequest(list);
    }
    public static BusinessHourSaveUpdateListRequest createStartTimeIsLaterThanEndTimeBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateInfo businessHour1 = new BusinessHourSaveUpdateInfo(
                java.time.LocalTime.of(16,0), java.time.LocalTime.of(9,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        ArrayList<BusinessHourSaveUpdateInfo> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourSaveUpdateListRequest request = new BusinessHourSaveUpdateListRequest(businessHourList);
        return request;
    }
    public static BusinessHourSaveUpdateListRequest createBreakStartTimeIsLaterThanEndTimeBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateInfo businessHour1 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(11,0)
        );
        ArrayList<BusinessHourSaveUpdateInfo> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourSaveUpdateListRequest request = new BusinessHourSaveUpdateListRequest(businessHourList);

        return request;
    }

    public static BusinessHourSaveUpdateListRequest createNullTimeBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateInfo businessHour1 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                true, null,null
        );
        ArrayList<BusinessHourSaveUpdateInfo> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourSaveUpdateListRequest request = new BusinessHourSaveUpdateListRequest(businessHourList);


        return request;
    }

    public static BusinessHourSaveUpdateListRequest createInvalidTimeBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateInfo businessHour1 = new BusinessHourSaveUpdateInfo(
                null, java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        ArrayList<BusinessHourSaveUpdateInfo> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourSaveUpdateListRequest request = new BusinessHourSaveUpdateListRequest(businessHourList);


        return request;
    }
    public static BusinessHourSaveUpdateListRequest createUpdateBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateInfo request1 = new BusinessHourSaveUpdateInfo(
                java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), DayOfWeek.SUNDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        BusinessHourSaveUpdateInfo request2 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), DayOfWeek.MONDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        BusinessHourSaveUpdateInfo request3 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), DayOfWeek.THURSDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        BusinessHourSaveUpdateInfo request4 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), DayOfWeek.TUESDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        BusinessHourSaveUpdateInfo request5 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), DayOfWeek.SATURDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        BusinessHourSaveUpdateInfo request6 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), DayOfWeek.WEDNESDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        BusinessHourSaveUpdateInfo request7 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), DayOfWeek.FRIDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        ArrayList<BusinessHourSaveUpdateInfo> businessHourList = new ArrayList<>();
        businessHourList.add(request1);
        businessHourList.add(request2);
        businessHourList.add(request3);
        businessHourList.add(request4);
        businessHourList.add(request5);
        businessHourList.add(request6);
        businessHourList.add(request7);

        BusinessHourSaveUpdateListRequest request = new BusinessHourSaveUpdateListRequest(businessHourList);
        return request;
    }
    public static BusinessHourSaveUpdateListRequest createDuplicatedDayOfWeekBusinessHourRequest(Long storeId){
        DayOfWeek duplicatedDayOfWeek = DayOfWeek.MONDAY;
        BusinessHourSaveUpdateInfo duplicatedDayOfWeekRequest1 = new BusinessHourSaveUpdateInfo(
                java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), duplicatedDayOfWeek, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        BusinessHourSaveUpdateInfo duplicatedDayOfWeekRequest2 = new BusinessHourSaveUpdateInfo(
                 java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), duplicatedDayOfWeek, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        ArrayList<BusinessHourSaveUpdateInfo> businessHourList = new ArrayList<>();
        businessHourList.add(duplicatedDayOfWeekRequest1);
        businessHourList.add(duplicatedDayOfWeekRequest2);
        BusinessHourSaveUpdateListRequest request = new BusinessHourSaveUpdateListRequest(businessHourList);
        return request;
    }

    //mock file

    // Image Constants
    public static final MockMultipartFile IMAGE_REQUEST_PART =
            new MockMultipartFile("storeImageList", "image1.jpg", "image/jpeg", "test data".getBytes());
    public static final MockMultipartFile IMAGE_REQUEST_PART2 =
            new MockMultipartFile("storeImageList", "imageS2.jpg", "image/jpeg", "test data".getBytes());

    //store reject reason
    public static final RejectReason STORE_REJECT_REASON = new RejectReason("입력 사항 불충족");

    public static final List<MyStoreResponse> MY_STORE_RESPONSES = Arrays.asList(
            MyStoreResponse.builder()
                    .storeId(1L)
                    .phone("010-1234-124")
                    .requestDate(LocalDate.now())
                    .reasonForRejection("")
                    .storeImageUrl("url1")
                    .isApproved(false)
                    .storeName("Store 1")
                    .address("Address 1")
                    .build(),
            MyStoreResponse.builder()
                    .storeId(2L)
                    .phone("010-1234-124")
                    .requestDate(LocalDate.now())
                    .reasonForRejection("")
                    .storeImageUrl("url1")
                    .isApproved(false)
                    .storeName("Store 2")
                    .address("Address 2")
                    .build(),
            MyStoreResponse.builder()
                    .storeId(3L)
                    .phone("010-1234-124")
                    .requestDate(LocalDate.now())
                    .reasonForRejection("")
                    .storeImageUrl("url1")
                    .isApproved(false)
                    .storeName("Store 3")
                    .address("Address 3")
                    .build()
    );
    // store

}



