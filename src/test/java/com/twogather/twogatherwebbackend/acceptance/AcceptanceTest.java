package com.twogather.twogatherwebbackend.acceptance;


import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.Tokens;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.domain.Category;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.dto.LoginResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveListRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateRequest;
import com.twogather.twogatherwebbackend.repository.CategoryRepository;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.MemberRepository;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.MultiPartSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.util.TestUtil.convert;
import static io.restassured.RestAssured.UNDEFINED_PORT;
import static io.restassured.RestAssured.given;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class AcceptanceTest
{
    @LocalServerPort
    public int port;
    @Autowired
    protected PrivateConstants constants;
    protected ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    protected ConsumerRepository consumerRepository;
    @MockBean
    protected BizRegNumberValidator validator;
    @Autowired
    private DatabaseCleanup databaseCleanup;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected ReviewRepository reviewRepository;
    @Autowired
    protected StoreRepository storeRepository;
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    protected MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        if(RestAssured.port == UNDEFINED_PORT){
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
        amazonS3.createBucket("store");
    }
    protected Tokens adminToken;
    protected Tokens ownerToken;
    protected Tokens consumerToken;
    protected MemberResponse memberResponse;
    protected Long storeId;
    protected Long consumerId;
    protected Long loginMemberId;

    protected <T> ValidatableResponse doGet(String path) {
        return given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }
    protected <T> ValidatableResponse doGet(String path,String refreshToken, String accessToken) {
        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }
    protected <T> ValidatableResponse doDelete(String path,String refreshToken, String accessToken) {
        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .when()
                .delete(path)
                .then()
                .log().all();
    }
    protected <T> ValidatableResponse doPatch(String path,String refreshToken, String accessToken) {
        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .when()
                .patch(path)
                .then()
                .log().all();
    }
    protected <T> ValidatableResponse doPatch(String path,String refreshToken, String accessToken, T request) {
        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .patch(path)
                .then()
                .log().all();
    }
    protected <T, R> R doPost(String path, String refreshToken, String accessToken, T request, Class<R> responseClass) {

        com.twogather.twogatherwebbackend.dto.Response response = given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(com.twogather.twogatherwebbackend.dto.Response.class);

        return objectMapper.convertValue(response.getData(), responseClass);
    }


    protected <T> ValidatableResponse doPost(String path, String refreshToken, String accessToken, T request) {

        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post(path)
                .then()
                .log().all();

    }
    protected <T> ValidatableResponse doPut(String path, String refreshToken, String accessToken, T request) {

        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .put(path)
                .then()
                .log().all();

    }
    protected <T, R> R doPost(String path, T request, Class<R> responseClass) {
        com.twogather.twogatherwebbackend.dto.Response response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(path)
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(com.twogather.twogatherwebbackend.dto.Response.class);
        return objectMapper.convertValue(response.getData(), responseClass);
    }

    protected <T> ValidatableResponse doPost(String path, T request) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(path)
                .then()
                .log().all();
    }
    protected <T> ValidatableResponse doPost(String path) {
        return given()
                .when()
                .post(path)
                .then()
                .log().all();
    }
    protected <T> ValidatableResponse doPost(String path,  String refreshToken, String accessToken) {
        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .when()
                .post(path)
                .then()
                .log().all();
    }


    protected <T> Tokens doLogin(T request) {
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/login")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        Headers headers = response.headers();

        LoginResponse loginResponse = convert(response.as(com.twogather.twogatherwebbackend.dto.Response.class), new TypeReference<LoginResponse>() {});
        loginMemberId = loginResponse.getMemberId();

        String accessToken = headers.getValue(constants.ACCESS_TOKEN_HEADER);
        String refreshToken = headers.getValue(constants.REFRESH_TOKEN_HEADER);

        return new Tokens(accessToken, refreshToken);
    }
    protected <T> ValidatableResponse failLogin(T request) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/login")
                .then()
                .log().all();
    }
    protected void validatorWillPass(){
        org.mockito.Mockito.when(validator.validateBizRegNumber(org.mockito.ArgumentMatchers.any(),org.mockito.ArgumentMatchers.any(),org.mockito.ArgumentMatchers.any())).thenReturn(true);
    }

    protected void registerOwner(){
        log.info("register owner");
        memberResponse = doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        ownerToken = doLogin(OWNER_LOGIN_REQUEST);
    }
    protected Long registerReview(){
        Store store = storeRepository.findActiveStoreById(storeId).get();
        Member member = memberRepository.findActiveMemberById(consumerId).get();
        return reviewRepository.save(new Review(store, member, "맛있어요", 3.2, LocalDate.now())).getReviewId();
    }
    protected void registerStore() {
        log.info("register store");
        validatorWillPass();
        Long categoryId = registerCategory();

        String storeRequestPart = null;
        String businessHourRequestPart = null;
        String keywordListPart = null;
        String menuRequestPart = null;
        try {
            storeRequestPart = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(STORE_SAVE_REQUEST);
            businessHourRequestPart = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(BUSINESS_HOUR_SAVE_UPDATE_REQUEST_LIST);
             keywordListPart = objectMapper.writeValueAsString(KEYWORD_LIST);
             menuRequestPart = objectMapper.writeValueAsString(MENU_SAVE_LIST_REQUEST);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        MultiPartSpecification storeRequestMultiPart = new MultiPartSpecBuilder(storeRequestPart)
                .controlName("storeRequest")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8)
                .build();

        MultiPartSpecification businessHourRequestMultiPart = new MultiPartSpecBuilder(businessHourRequestPart)
                .controlName("businessHourRequest")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8)
                .build();

        MultiPartSpecification keywordListMultiPart = new MultiPartSpecBuilder(keywordListPart)
                .controlName("keywordList")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8)
                .build();

        MultiPartSpecification menuRequestMultiPart = new MultiPartSpecBuilder(menuRequestPart)
                .controlName("menuRequest")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8)
                .build();

        storeId = objectMapper.convertValue(given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getRefreshToken())
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getAccessToken())
                .multiPart(storeRequestMultiPart)
                .multiPart(businessHourRequestMultiPart)
                .multiPart(keywordListMultiPart)
                .multiPart(menuRequestMultiPart)
                .multiPart("storeImageList", createFile(), "multipart/form-data")
                .contentType("multipart/form-data")
                .accept(ContentType.JSON)
                .when()
                .post("/api/stores/categories/{categoryId}", categoryId)
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(com.twogather.twogatherwebbackend.dto.Response.class).getData(),  StoreResponse.class).getStoreId();
    }

    protected ValidatableResponse registerStore(StoreSaveUpdateRequest storeRequest,
                                 BusinessHourSaveUpdateListRequest businessHourRequest,
                                 List<String> keywordList,
                                 MenuSaveListRequest menuRequest) {
        log.info("register store");
        validatorWillPass();
        Long categoryId = registerCategory();

        String storeRequestPart = null;
        String businessHourRequestPart = null;
        String keywordListPart = null;
        String menuRequestPart = null;
        try {
            storeRequestPart = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(storeRequest);
            businessHourRequestPart = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(businessHourRequest);
            keywordListPart = objectMapper.writeValueAsString(keywordList);
            menuRequestPart = objectMapper.writeValueAsString(menuRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        MultiPartSpecification storeRequestMultiPart = new MultiPartSpecBuilder(storeRequestPart)
                .controlName("storeRequest")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8)
                .build();

        MultiPartSpecification businessHourRequestMultiPart = new MultiPartSpecBuilder(businessHourRequestPart)
                .controlName("businessHourRequest")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8)
                .build();

        MultiPartSpecification keywordListMultiPart = new MultiPartSpecBuilder(keywordListPart)
                .controlName("keywordList")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8)
                .build();

        MultiPartSpecification menuRequestMultiPart = new MultiPartSpecBuilder(menuRequestPart)
                .controlName("menuRequest")
                .mimeType("application/json")
                .charset(StandardCharsets.UTF_8)
                .build();

        return given()
                        .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getRefreshToken())
                        .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getAccessToken())
                        .multiPart("storeImageList", createFile(), "multipart/form-data")
                        .multiPart(storeRequestMultiPart)
                        .multiPart(businessHourRequestMultiPart)
                        .multiPart(keywordListMultiPart)
                        .multiPart(menuRequestMultiPart)
                        .contentType("multipart/form-data;charset=UTF-8")
                        .accept(ContentType.JSON)
                        .when()
                        .post("/api/stores/categories/{categoryId}", categoryId)
                        .then()
                        .log().all();
    }
    protected Long registerCategory(){
        Category category = categoryRepository.save(new Category("기타"));
        return category.getCategoryId();
    }
    protected void approveStore(){
        log.info("approve store");
        consumerRepository.save(ADMIN);
        adminToken = doLogin(ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/approve/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken());
    }
    protected void registerConsumer(){
        loginMemberId = consumerId = consumerRepository.save(CONSUMER).getMemberId();
        consumerToken = doLogin(CONSUMER_LOGIN_REQUEST);
    }
    protected void leaveOwner(){
        log.info("leave owner");
        String leaveMemberUrl = OWNER_URL+"/" + memberResponse.getMemberId();
        doDelete(leaveMemberUrl, ownerToken.getRefreshToken(), ownerToken.getAccessToken());
    }
    protected void leaveConsumer(){
        String leaveMemberUrl = CONSUMER_URL+"/" + loginMemberId;
        doDelete(leaveMemberUrl, consumerToken.getRefreshToken(), consumerToken.getAccessToken());
    }

    private File createFile(){
        File file = null;
        try {
            // Create temp file
            file = File.createTempFile("tempfile", ".txt");

            // Write to temp file
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("This is a dummy file");
            bw.close();

            // Delete file when program ends
            file.deleteOnExit();

        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
        }
        return file;
    }
}
