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

    public static final String OWNER_USERNAME = "owner1";
    public static final String OWNER_EMAIL = "asd@naver.com";
    public static final String OWNER_PASSWORD = "asd@asd@@123";
    public static final String OWNER_NAME = "루터";
    public static final String OWNER_BUSINESS_NAME = "루터";
    public static final String OWNER_BUSINESS_NUMBER = "0000000000";
    public static final LocalDate OWNER_BUSINESS_START_DATE = LocalDate.now();

    public static final String CONSUMER_USERNAME = "consumer1";
    public static final String CONSUMER_EMAIL = "consumer@naver.com";
    public static final String CONSUMER_PASSWORD = "asd!asd123";
    public static final String CONSUMER_NAME = "김소비";
    public static final Consumer CONSUMER = new Consumer(CONSUMER_USERNAME,CONSUMER_EMAIL,
            passwordEncoded.encode(CONSUMER_PASSWORD), CONSUMER_NAME, com.twogather.twogatherwebbackend.domain.AuthenticationType.CONSUMER, true);

    public static final String MEMBER_EMAIL = "pobi@email.com";
    public static final String MEMBER_PASSWORD = "test1234!!!";
    public static final String MEMBER_NAME = "루터";

    public static final String STORE_NAME = "김가네 닭갈비";
    public static final String STORE_ADDRESS = "전주시 평화동 어쩌고 222-2";
    public static final String STORE_PHONE = "063-231-2222";


    public static final String WRONG_PASSWORD = "1asd@@@SAdad@@";

    public static final String AUTH = "Authorization";

    public static final String MEMBER_USERNAME = "member1";
    public static final Long BUSINESS_HOUR_ID = 1l;
    public static final Member MEMBER =
            new Member(1l,MEMBER_USERNAME, MEMBER_EMAIL, MEMBER_PASSWORD, MEMBER_NAME, AuthenticationType.STORE_OWNER,true);

    public static final ConsumerResponse CONSUMER_RESPONSE
            = new ConsumerResponse(1l, MEMBER_USERNAME,"김멍치","sda@naer.com");
    public static final LoginRequest OWNER_LOGIN_REQUEST = new LoginRequest(OWNER_EMAIL, OWNER_USERNAME,OWNER_PASSWORD);
    public static final LoginRequest OWNER_INVALID_LOGIN_REQUEST = new LoginRequest(OWNER_EMAIL, OWNER_USERNAME, WRONG_PASSWORD);
    public static final LoginRequest CONSUMER_LOGIN_REQUEST = new LoginRequest(CONSUMER_EMAIL, CONSUMER_USERNAME,CONSUMER_PASSWORD);
    public static final ConsumerSaveUpdateRequest CONSUMER_SAVE_UPDATE_REQUEST = new ConsumerSaveUpdateRequest(CONSUMER_USERNAME,CONSUMER_EMAIL, CONSUMER_PASSWORD, CONSUMER_NAME);
    public static final ConsumerSaveUpdateRequest CONSUMER_SAVE_UPDATE_REQUEST2 = new ConsumerSaveUpdateRequest("user2","ASD@NA.COM", CONSUMER_PASSWORD, CONSUMER_NAME);

    public static final StoreOwnerSaveUpdateRequest OWNER_SAVE_REQUEST =
            new StoreOwnerSaveUpdateRequest(
                    OWNER_USERNAME, OWNER_EMAIL, OWNER_PASSWORD, OWNER_NAME,
                    OWNER_BUSINESS_NUMBER, OWNER_BUSINESS_NAME, OWNER_BUSINESS_START_DATE
            );
    public static final StoreOwnerSaveUpdateRequest OWNER_SAVE_REQUEST2 =
            new StoreOwnerSaveUpdateRequest(
                    "user2", "sd@naer.com", OWNER_PASSWORD, OWNER_NAME,
                    OWNER_BUSINESS_NUMBER, OWNER_BUSINESS_NAME, OWNER_BUSINESS_START_DATE
            );
    public static final StoreOwnerSaveUpdateRequest INVALID_OWNER_SAVE_REQUEST =
            new StoreOwnerSaveUpdateRequest(
                    "2asd","sd@@@", "AA", "!!!!!",
                    "111", "AA!!!", null
            );
    public static final BusinessHourSaveUpdateRequest INVALID_BUSINESS_HOUR_SAVE_REQUEST =
            new BusinessHourSaveUpdateRequest(INVALID_STORE_ID, END_TIME, START_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

    public static final StoreSaveUpdateRequest STORE_SAVE_REQUEST =
            new StoreSaveUpdateRequest(STORE_NAME, STORE_ADDRESS, STORE_PHONE);
    public static final StoreOwner STORE_OWNER =
            new StoreOwner(OWNER_USERNAME, OWNER_EMAIL, passwordEncoded.encode(OWNER_PASSWORD), OWNER_NAME,
                    OWNER_BUSINESS_NUMBER, OWNER_BUSINESS_NAME, OWNER_BUSINESS_START_DATE,
                    com.twogather.twogatherwebbackend.domain.AuthenticationType.STORE_OWNER, true);

    public static final BusinessHourSaveUpdateRequest BUSINESS_HOUR_SAVE_UPDATE_REQUEST =
            new BusinessHourSaveUpdateRequest(STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

    public static final BusinessHourResponse BUSINESS_HOUR_RESPONSE =
            new BusinessHourResponse(BUSINESS_HOUR_ID, STORE_ID, START_TIME, END_TIME, DAY_OF_WEEK, IS_OPEN, false, null,null);

    public static final StoreSaveUpdateResponse STORE_SAVE_UPDATE_RESPONSE =
            new StoreSaveUpdateResponse(1l, "가게이름", "전주시 평화동 산동 2길 1-3","010-1234-1234");

    public static final StoreResponse STORE_RESPONSE =
            new StoreResponse(1l, "가게이름", "전주시 평화동 산동 2길 1-3","010-1234-1234", "url1");
    public static final ArrayList KEYWORD_LIST =
            new ArrayList<>(){{
                add("분위기좋은");
                add("사진찍기좋은");
                add("저렴한");
            }};
    public static final StoreResponseWithKeyword STORES_RESPONSE =
            new StoreResponseWithKeyword(1l, "가게이름", "전주시 평화동 산동 2길 1-3",4.2,KEYWORD_LIST, "imageurl1");
   public static final StoreSaveUpdateRequest STORE_REQUEST =
            new StoreSaveUpdateRequest("가게이름", "전주시 평화동 산동 2길 1-3","010-1234-1234");
    public static final StoreSaveUpdateRequest STORE_UPDATE_REQUEST =
            new StoreSaveUpdateRequest("가게이름", "전주시 평화동 산동 2길 1-3","010-1234-1234");

    public static final StoreOwnerSaveUpdateRequest STORE_OWNER_REQUEST =
            new StoreOwnerSaveUpdateRequest(OWNER_USERNAME, "sad@baer.co", "p23dasdaw","사업자이름", "0000000000", "이름",LocalDate.now());
    public static final StoreOwnerResponse STORE_OWNER_RESPONSE =
            new StoreOwnerResponse(1l, "hello1","홍길동", "ifd@naebr.com", "0000000000", "홍길동", LocalDate.of(2000,01,01));
    public static final LocalDate DATE = LocalDate.parse("2020-02-02");
    public static final MyStoreResponse MY_STORES_RESPONSE =
            new MyStoreResponse(1l, "가게이름", "전주시 평화동 산동 2길 1-3",
                    "063-231-4990",false, "자격미달", DATE, "url1");

    public static final Page<MyReviewInfoResponse> MY_REVIEW_LIST = new PageImpl<>(List.of(
            new MyReviewInfoResponse(1L,  "Good product", 4.5, LocalDate.of(2022, 1, 1), "imageurl", "김가네삼겹살", "서울시 어쩌고 어쩌고", "김은지"),
            new MyReviewInfoResponse(1L, "Not bad", 3.0, LocalDate.of(2022, 1, 3), "imageurl2", "레스토랑1", "경기도 어쩌고 어쩌고", "박은지"),
            new MyReviewInfoResponse(2L, "Excellent", 5.0, LocalDate.of(2022, 1, 5), "imageurl3", "김밥집1", "전주시 어쩌고 어쩌고", "김지은")
    ));
    public static final ReviewResponse REVIEW_RESPONSE =
            new ReviewResponse(1L, 1L,"너무 맛있어요~!" , 3.2, DATE);
    public static final ReviewSaveRequest REVIEW_SAVE_REQUEST =
            new ReviewSaveRequest(1l,1l,"진짜맛있어요!", 1.2);
    public static final ReviewUpdateRequest REVIEW_UPDATE_REQUEST =
            new ReviewUpdateRequest(1l,1l,"진짜맛있어요!", 1.2);
    public static final MockMultipartFile IMAGE1
            = new MockMultipartFile("fileList", "image1.jpg", "image/jpeg", "test data".getBytes());
    public static final MockMultipartFile IMAGE2
            = new MockMultipartFile("fileList", "imageS2.jpg", "image/jpeg", "test data".getBytes());
    public static final LoginRequest LOGIN_REQUEST
            = new LoginRequest(OWNER_EMAIL, OWNER_USERNAME,OWNER_PASSWORD);
    public static final ImageIdList IMAGE_ID_LIST =
            new ImageIdList(
                    new ArrayList<>(){{
                        add(1l);
                        add(2l);
                        add(3l);
                    }});
    public static final CommentSaveUpdateRequest COMMENT_SAVE_UPDATE_REQUEST
            = new CommentSaveUpdateRequest("내용내뇽ㅇ");
    public static final CommentResponse COMMENT_RESPONSE =
            new CommentResponse("대댓글내용", false, LocalDate.now());
    public static final ImageResponse IMAGE_RESPONSE =
            new ImageResponse(1l, "www.maver/ssd/c");
    public static final String VALID_EMAIL = "firefly_0@naver.com";
    public static final String INVALID_EMAIL = "firefASDly_0@naver.com";
    public static final VerificationCodeResponse VERIFICATION_CODE_RESPONSE = new VerificationCodeResponse("asVVaa");
    public static final EmailRequest EMAIL_REQUEST = new EmailRequest(VALID_EMAIL);
    public static final ArrayList CATEGORY_RESPONSE_LIST =
            new ArrayList<CategoryResponse>(){{
                add(new CategoryResponse(1l,"양식"));
                add(new CategoryResponse(2l,"일식"));
                add(new CategoryResponse(3l,"분식"));
            }};
    public static final CategoryResponse CATEGORY_RESPONSE =
            new CategoryResponse(1l,"양식");
    public static final List IMAGE_RESPONSE_LIST =
            new ArrayList<ImageResponse>(){{
                add(IMAGE_RESPONSE);
                add(IMAGE_RESPONSE);
                add(IMAGE_RESPONSE);
            }};
    public static final ArrayList STORES_RESPONSE_LIST =
            new ArrayList<StoreResponseWithKeyword>(){{
                add(STORES_RESPONSE);
                add(STORES_RESPONSE);
                add(STORES_RESPONSE);
            }};

    public static final List<TopStoreResponse> STORES_TOP3_TOP_RATED_RESPONSE =
            Arrays.asList(
                    new TopStoreResponse(1l,"store1", 4.5, "123 Main St.", "url1"),
                    new TopStoreResponse(1l,"store2", 4.2, "456 Oak Ave.", "url2"),
                    new TopStoreResponse(1l,"store3", 4.0, "789 Elm St.", "url3")
            );
    public static final List<TopStoreResponse> STORES_TOP3_MOST_REVIEWED_RESPONSE =
            Arrays.asList(
                    new TopStoreResponse(1l,"store11", 4.8, "123 Main St.", "url11"),
                    new TopStoreResponse(1l,"store12", 4.6, "456 Oak Ave.", "url12"),
                    new TopStoreResponse(1l,"store13", 4.4, "789 Elm St.", "url13")
            );
    public static final List<TopStoreResponse> STORES_TOP10_RESPONSE_LIST =
            Arrays.asList(
                    new TopStoreResponse(1l,"store1", 4.5, "123 Main St.", "url1"),
                    new TopStoreResponse(2l,"store2", 4.2, "456 Oak Ave.", "url2"),
                    new TopStoreResponse(3l,"store3", 3.0, "789 Elm St.", "url3"),
                    new TopStoreResponse(4l,"store4", 3.5, "123 Main St.", "url1"),
                    new TopStoreResponse(5l,"store5", 3.2, "456 Oak Ave.", "url2"),
                    new TopStoreResponse(6l,"store6", 3.0, "789 Elm St.", "url3"),
                    new TopStoreResponse(7l,"store7", 3.5, "123 Main St.", "url1"),
                    new TopStoreResponse(8l,"store8", 2.2, "456 Oak Ave.", "url2"),
                    new TopStoreResponse(9l,"store9", 1.0, "789 Elm St.", "url3")
            );
    public static final Page<MyStoreResponse> MY_STORES_RESPONSE_PAGE = new PageImpl<>(List.of(
            MY_STORES_RESPONSE,
            MY_STORES_RESPONSE,
            MY_STORES_RESPONSE
    ));

    public static final ArrayList<MenuResponse> MENU_RESPONSE_LIST =
            new ArrayList<MenuResponse>(){{
                add(new MenuResponse(1l,"감자",1000));
                add(new MenuResponse(2l,"케찹",12000));
                add(new MenuResponse(3l,"햄버기",11000));
            }};
    public static final ArrayList MENU_SAVE_LIST =
            new ArrayList<MenuSaveInfo>(){{
                add(new MenuSaveInfo("감자",1000));
                add(new MenuSaveInfo("케찹",12000));
                add(new MenuSaveInfo("햄버기",11000));
            }};
    public static final MenuSaveListRequest MENU_SAVE_LIST_REQUEST
            =new MenuSaveListRequest(MENU_SAVE_LIST);
    public static final ArrayList MENU_UPDATE_LIST =
            new ArrayList<MenuUpdateInfo>(){{
                add(new MenuUpdateInfo(1l,"감자",1000));
                add(new MenuUpdateInfo(2l,"케찹",12000));
                add(new MenuUpdateInfo(3l,"햄버기",11000));
            }};
    public static final MenuUpdateListRequest MENU_UPDATE_LIST_REQUEST=
            new MenuUpdateListRequest(MENU_UPDATE_LIST);
    public static final ArrayList BUSINESS_HOUR_RESPONSE_LIST =
            new ArrayList<BusinessHourResponse>(){{
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
                add(BUSINESS_HOUR_RESPONSE);
            }};
    public static final ArrayList BUSINESS_HOUR_SAVE_UPDATE_LIST =
            new ArrayList<BusinessHourSaveUpdateRequest>(){{
                add(BUSINESS_HOUR_SAVE_UPDATE_REQUEST);
                add(BUSINESS_HOUR_SAVE_UPDATE_REQUEST);
                add(BUSINESS_HOUR_SAVE_UPDATE_REQUEST);
            }};
    public static final BusinessHourController.BusinessHourSaveUpdateListRequest BUSINESS_HOUR_SAVE_UPDATE_REQUEST_LIST
            = new BusinessHourController.BusinessHourSaveUpdateListRequest(BUSINESS_HOUR_SAVE_UPDATE_LIST);
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
