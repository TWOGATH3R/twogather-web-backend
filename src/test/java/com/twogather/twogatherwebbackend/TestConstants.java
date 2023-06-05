package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.controller.BusinessHourController;
import com.twogather.twogatherwebbackend.controller.MenuController;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourIdList;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.category.CategoryResponse;
import com.twogather.twogatherwebbackend.dto.comment.CommentResponse;
import com.twogather.twogatherwebbackend.dto.comment.CommentSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.email.EmailRequest;
import com.twogather.twogatherwebbackend.dto.email.VerificationCodeResponse;
import com.twogather.twogatherwebbackend.dto.image.ImageIdList;
import com.twogather.twogatherwebbackend.dto.image.ImageResponse;
import com.twogather.twogatherwebbackend.dto.member.*;
import com.twogather.twogatherwebbackend.dto.menu.MenuResponse;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveRequest;
import com.twogather.twogatherwebbackend.dto.menu.MenuUpdateRequest;
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

    // Business Hour Constants
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

    // Owner Constants
    public static final String OWNER_USERNAME = "owner1";
    public static final String OWNER_EMAIL = "asd@naver.com";
    public static final String OWNER_PASSWORD = "asd@asd@@123";
    public static final String OWNER_NAME = "루터";
    public static final MemberSaveUpdateRequest OWNER_SAVE_UPDATE_REQUEST = new MemberSaveUpdateRequest(
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
    public static final String CONSUMER_PASSWORD = "asd!asd123";
    public static final String CONSUMER_NAME = "김소비";
    public static final MemberSaveUpdateRequest CONSUMER_SAVE_UPDATE_REQUEST =
            new MemberSaveUpdateRequest(CONSUMER_EMAIL, CONSUMER_USERNAME, CONSUMER_PASSWORD, CONSUMER_NAME);
    public static final Consumer CONSUMER = new Consumer(CONSUMER_USERNAME, CONSUMER_EMAIL,
            passwordEncoder.encode(CONSUMER_PASSWORD), CONSUMER_NAME, AuthenticationType.CONSUMER, true);

    // Member Constants
    public static final String MEMBER_EMAIL = "pobi@email.com";
    public static final String MEMBER_PASSWORD = "test1234!!!";
    public static final String MEMBER_NAME = "루터";

    // Common Constants
    public static final String WRONG_PASSWORD = "1asd@@@SAdad@@";
    public static final String AUTH = "Authorization";

    // Member Constants
    public static final String MEMBER_USERNAME = "member1";
    public static final Member MEMBER =
            new Member(1l, MEMBER_USERNAME, MEMBER_EMAIL, MEMBER_PASSWORD, MEMBER_NAME, AuthenticationType.STORE_OWNER, true);

    // Login Requests
    public static final LoginRequest OWNER_LOGIN_REQUEST = new LoginRequest( OWNER_USERNAME, OWNER_PASSWORD);
    public static final LoginRequest OWNER_INVALID_LOGIN_REQUEST = new LoginRequest(OWNER_USERNAME, WRONG_PASSWORD);
    public static final LoginRequest CONSUMER_LOGIN_REQUEST = new LoginRequest( CONSUMER_USERNAME, CONSUMER_PASSWORD);
    public static final LoginRequest ADMIN_LOGIN_REQUEST = new LoginRequest( ADMIN_USERNAME, ADMIN_PASSWORD);

    // Business Hour Save/Update Request
    public static final BusinessHourSaveUpdateRequest INVALID_BUSINESS_HOUR_SAVE_REQUEST =
            new BusinessHourSaveUpdateRequest(INVALID_STORE_ID, END_TIME, START_TIME, DAY_OF_WEEK, IS_OPEN, false, null, null);
    public static final BusinessHourSaveUpdateRequest BUSINESS_HOUR_SAVE_UPDATE_REQUEST =
            new BusinessHourSaveUpdateRequest(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null, null);

    // Business Hour Response
    public static final Long BUSINESS_HOUR_ID = 1l;
    public static final BusinessHourResponse BUSINESS_HOUR_RESPONSE =
            new BusinessHourResponse(BUSINESS_HOUR_ID, STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null, null);

    // Store Save/Update Request
    public static final StoreSaveUpdateRequest STORE_SAVE_REQUEST =
            new StoreSaveUpdateRequest(STORE_NAME, STORE_ADDRESS, STORE_PHONE, "0000000000", "홍길동", LocalDate.now());
    public static final StoreSaveUpdateRequest STORE_SAVE_REQUEST2 =
            new StoreSaveUpdateRequest("other store", STORE_ADDRESS, STORE_PHONE, "0000000000", "홍길동", LocalDate.now());

    public static final StoreOwner STORE_OWNER =
            new StoreOwner(OWNER_USERNAME, OWNER_EMAIL, passwordEncoder.encode(OWNER_PASSWORD), OWNER_NAME,
                    AuthenticationType.STORE_OWNER, true);
    public static final StoreSaveUpdateResponse STORE_SAVE_UPDATE_RESPONSE =
            new StoreSaveUpdateResponse(1l, "가게이름", "전주시 평화동 산동 2길 1-3", "010-1234-1234");

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

    // Image Constants
    public static final MockMultipartFile IMAGE1 =
            new MockMultipartFile("fileList", "image1.jpg", "image/jpeg", "test data".getBytes());
    public static final MockMultipartFile IMAGE2 =
            new MockMultipartFile("fileList", "imageS2.jpg", "image/jpeg", "test data".getBytes());


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
            new CommentResponse("대댓글내용", false, LocalDate.now());

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

    // Store Response with Keyword
    public static final ArrayList<String> KEYWORD_LIST = new ArrayList<>(Arrays.asList(
            "분위기좋은", "사진찍기좋은", "저렴한"
    ));
    public static final StoreResponseWithKeyword STORES_RESPONSE =
            new StoreResponseWithKeyword(1l, "가게이름", "전주시 평화동 산동 2길 1-3", 4.2, KEYWORD_LIST, "imageurl1");
    public static final ArrayList STORES_RESPONSE_LIST =
            new ArrayList<StoreResponseWithKeyword>(){{
                add(STORES_RESPONSE);
                add(STORES_RESPONSE);
                add(STORES_RESPONSE);
            }};

    // Store Save/Update Request
    public static final StoreSaveUpdateRequest STORE_REQUEST =
            new StoreSaveUpdateRequest("가게이름", "전주시 평화동 산동 2길 1-3", "010-1234-1234", "0000000000", "홍길동", LocalDate.now());
    public static final StoreSaveUpdateRequest STORE_UPDATE_REQUEST =
            new StoreSaveUpdateRequest("가게이름", "전주시 평화동 산동 2길 1-3", "010-1234-1234", "0000000000", "홍길동", LocalDate.now());

    // My Store Response Page
    public static final Page<MyStoreResponse> MY_STORES_RESPONSE_PAGE =
            new PageImpl<>(List.of(MY_STORES_RESPONSE, MY_STORES_RESPONSE, MY_STORES_RESPONSE));

    // Top Store Response
    public static final List<TopStoreResponse> STORES_TOP3_TOP_RATED_RESPONSE = Arrays.asList(
            new TopStoreResponse(1l, "store1", 4.5, "123 Main St.", "url1"),
            new TopStoreResponse(1l, "store2", 4.2, "456 Oak Ave.", "url2"),
            new TopStoreResponse(1l, "store3", 4.0, "789 Elm St.", "url3")
    );
    public static final List<TopStoreResponse> STORES_TOP3_MOST_REVIEWED_RESPONSE = Arrays.asList(
            new TopStoreResponse(1l, "store11", 4.8, "123 Main St.", "url11"),
            new TopStoreResponse(1l, "store12", 4.6, "456 Oak Ave.", "url12"),
            new TopStoreResponse(1l, "store13", 4.4, "789 Elm St.", "url13")
    );
    public static final List<TopStoreResponse> STORES_TOP10_RESPONSE_LIST = Arrays.asList(
            new TopStoreResponse(1l, "store1", 4.5, "123 Main St.", "url1"),
            new TopStoreResponse(2l, "store2", 4.2, "456 Oak Ave.", "url2"),
            new TopStoreResponse(3l, "store3", 3.0, "789 Elm St.", "url3"),
            new TopStoreResponse(4l, "store4", 3.5, "123 Main St.", "url1"),
            new TopStoreResponse(5l, "store5", 3.2, "456 Oak Ave.", "url2"),
            new TopStoreResponse(6l, "store6", 3.0, "789 Elm St.", "url3"),
            new TopStoreResponse(7l, "store7", 3.5, "123 Main St.", "url1"),
            new TopStoreResponse(8l, "store8", 2.2, "456 Oak Ave.", "url2"),
            new TopStoreResponse(9l, "store9", 1.0, "789 Elm St.", "url3")
    );

    // Menu Constants
    public static final ArrayList<MenuResponse> MENU_RESPONSE_LIST = new ArrayList<>(Arrays.asList(
            new MenuResponse(1l, "감자", 1000),
            new MenuResponse(2l, "케찹", 2000)
    ));
    public static final ArrayList<MenuSaveRequest> MENU_SAVE_LIST = new ArrayList<>(Arrays.asList(
            new MenuSaveRequest("감자", 1000),
            new MenuSaveRequest("케찹", 2000)
    ));
    public static final ArrayList<MenuSaveRequest> MENU_SAVE_NULL_LIST = new ArrayList<>(Arrays.asList(
            new MenuSaveRequest("감자", 1000),
            new MenuSaveRequest(null, 2000)
    ));
    public static final ArrayList<MenuSaveRequest> MENU_SAVE_MINUS_VALUE_LIST = new ArrayList<>(Arrays.asList(
            new MenuSaveRequest("감자", -1000)
    ));
    public static final ArrayList<MenuUpdateRequest> MENU_UPDATE_NULL_LIST = new ArrayList<>(Arrays.asList(
            new MenuUpdateRequest(1l,"감자", 1000),
            new MenuUpdateRequest(2l,null, 2000)
    ));
    public static final ArrayList<MenuUpdateRequest> MENU_UPDATE_MINUS_VALUE_LIST = new ArrayList<>(Arrays.asList(
            new MenuUpdateRequest(1l,"감자", -1000)
    ));
    public static final MenuController.MenuSaveListRequest MENU_SAVE_LIST_REQUEST =
            new MenuController.MenuSaveListRequest(MENU_SAVE_LIST);
    public static final MenuController.MenuSaveListRequest MENU_SAVE_LIST_NULL_REQUEST =
            new MenuController.MenuSaveListRequest(MENU_SAVE_NULL_LIST);
    public static final MenuController.MenuUpdateListRequest MENU_UPDATE_LIST_NULL_REQUEST =
            new MenuController.MenuUpdateListRequest(MENU_UPDATE_NULL_LIST);
    public static final MenuController.MenuSaveListRequest MENU_SAVE_LIST_MINUS_VALUE_REQUEST =
            new MenuController.MenuSaveListRequest(MENU_SAVE_MINUS_VALUE_LIST);
    public static final MenuController.MenuUpdateListRequest MENU_UPDATE_LIST_MINUS_VALUE_REQUEST =
            new MenuController.MenuUpdateListRequest(MENU_UPDATE_MINUS_VALUE_LIST);
    public static final ArrayList<MenuUpdateRequest> MENU_UPDATE_LIST = new ArrayList<>(Arrays.asList(
            new MenuUpdateRequest(1l, "new 감자", 10000),
            new MenuUpdateRequest(2l, "new 케찹", 20000)
    ));
    public static final MenuController.MenuUpdateListRequest MENU_UPDATE_LIST_REQUEST =
            new MenuController.MenuUpdateListRequest(MENU_UPDATE_LIST);

    // Business Hour Constants
    public static final ArrayList<BusinessHourResponse> BUSINESS_HOUR_RESPONSE_LIST =
            new ArrayList<>(Arrays.asList(BUSINESS_HOUR_RESPONSE, BUSINESS_HOUR_RESPONSE, BUSINESS_HOUR_RESPONSE));
    public static final ArrayList<BusinessHourSaveUpdateRequest> BUSINESS_HOUR_SAVE_UPDATE_LIST =
            new ArrayList<>(Arrays.asList(BUSINESS_HOUR_SAVE_UPDATE_REQUEST, BUSINESS_HOUR_SAVE_UPDATE_REQUEST, BUSINESS_HOUR_SAVE_UPDATE_REQUEST));
    public static final BusinessHourController.BusinessHourSaveUpdateListRequest BUSINESS_HOUR_SAVE_UPDATE_REQUEST_LIST =
            new BusinessHourController.BusinessHourSaveUpdateListRequest(BUSINESS_HOUR_SAVE_UPDATE_LIST);
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
    public static final String LOGIN_URL = "/api/login";
    public static final String STORE_URL = "/api/stores";
    public static final String CATEGORY_URL = "/api/categories";

    //BusinessHour List
    public static BusinessHourController.BusinessHourSaveUpdateListRequest createBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
            storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour2 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.THURSDAY, true,
                false, null,null
        );
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        businessHourList.add(businessHour2);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

        return request;
    }

    public static BusinessHourController.BusinessHourSaveUpdateListRequest createBusinessHourRequestWithAllDayOfWeek(Long storeId){
        List<BusinessHourSaveUpdateRequest> list = new ArrayList<>();
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour2 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.TUESDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour3 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.WEDNESDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour4 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.THURSDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour5 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.SATURDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour6 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.FRIDAY, true,
                false, null,null
        );
        BusinessHourSaveUpdateRequest businessHour7 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), DayOfWeek.SUNDAY, true,
                false, null,null
        );
        list.add(businessHour1);
        list.add(businessHour2);
        list.add(businessHour3);
        list.add(businessHour4);
        list.add(businessHour5);
        list.add(businessHour6);
        list.add(businessHour7);

        return new BusinessHourController.BusinessHourSaveUpdateListRequest(list);
    }
    public static BusinessHourController.BusinessHourSaveUpdateListRequest createStartTimeIsLaterThanEndTimeBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(16,0), java.time.LocalTime.of(9,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);
        return request;
    }
    public static BusinessHourController.BusinessHourSaveUpdateListRequest createBreakStartTimeIsLaterThanEndTimeBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(11,0)
        );
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);

        return request;
    }

    public static BusinessHourController.BusinessHourSaveUpdateListRequest createNullTimeBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(9,0), java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                true, null,null
        );
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);


        return request;
    }

    public static BusinessHourController.BusinessHourSaveUpdateListRequest createInvalidTimeBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateRequest businessHour1 = new BusinessHourSaveUpdateRequest(
                storeId,null, java.time.LocalTime.of(16,0), java.time.DayOfWeek.MONDAY, true,
                false, null,null
        );
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(businessHour1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);


        return request;
    }
    public static BusinessHourController.BusinessHourSaveUpdateListRequest createUpdateBusinessHourRequest(Long storeId){
        BusinessHourSaveUpdateRequest request1 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), java.time.DayOfWeek.MONDAY, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(request1);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);
        return request;
    }
    public static BusinessHourController.BusinessHourSaveUpdateListRequest createDuplicatedDayOfWeekBusinessHourRequest(Long storeId){
        DayOfWeek duplicatedDayOfWeek = DayOfWeek.THURSDAY;
        BusinessHourSaveUpdateRequest duplicatedDayOfWeekRequest1 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), duplicatedDayOfWeek, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        BusinessHourSaveUpdateRequest duplicatedDayOfWeekRequest2 = new BusinessHourSaveUpdateRequest(
                storeId, java.time.LocalTime.of(11,0), java.time.LocalTime.of(17,0), duplicatedDayOfWeek, true,
                true, java.time.LocalTime.of(12,0),java.time.LocalTime.of(13,0)
        );
        ArrayList<BusinessHourSaveUpdateRequest> businessHourList = new ArrayList<>();
        businessHourList.add(duplicatedDayOfWeekRequest1);
        businessHourList.add(duplicatedDayOfWeekRequest2);
        BusinessHourController.BusinessHourSaveUpdateListRequest request = new BusinessHourController.BusinessHourSaveUpdateListRequest(businessHourList);
        return request;
    }

}



