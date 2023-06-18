package com.twogather.twogatherwebbackend.acceptance;


import com.amazonaws.services.s3.AmazonS3;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.Tokens;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.LoginResponse;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveListRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateResponse;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.*;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.util.TestUtil.convert;
import static io.restassured.RestAssured.UNDEFINED_PORT;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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
    @Autowired
    protected KeywordRepository keywordRepository;
    @Autowired
    protected ImageRepository imageRepository;

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
    protected Long categoryId;
    protected List<Keyword> keywordList;

    protected <T> ValidatableResponse doDelete(String path,String refreshToken, String accessToken) {
        return given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + refreshToken)
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + accessToken)
                .when()
                .delete(path)
                .then()
                .log().all();
    }
    protected <T> ValidatableResponse doGet(String path, String refreshToken, String accessToken) {

        RequestSpecification requestSpec = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        Optional.ofNullable(refreshToken).ifPresent(token -> requestSpec.header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + token));
        Optional.ofNullable(accessToken).ifPresent(token -> requestSpec.header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + token));

        return requestSpec
                .when()
                .get(path)
                .then()
                .log().all();
    }
    protected <T> ValidatableResponse doPost(String path, String refreshToken, String accessToken, T requestBody) {

        RequestSpecification requestSpec = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        Optional.ofNullable(refreshToken).ifPresent(token -> requestSpec.header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + token));
        Optional.ofNullable(accessToken).ifPresent(token -> requestSpec.header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + token));
        Optional.ofNullable(requestBody).ifPresent(requestSpec::body);

        return requestSpec
                .when()
                .post(path)
                .then()
                .log().all();
    }
    protected <T> ValidatableResponse doPut(String path, String refreshToken, String accessToken, T requestBody) {

        RequestSpecification requestSpec = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        Optional.ofNullable(refreshToken).ifPresent(token -> requestSpec.header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + token));
        Optional.ofNullable(accessToken).ifPresent(token -> requestSpec.header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + token));
        Optional.ofNullable(requestBody).ifPresent(requestSpec::body);

        return requestSpec
                .when()
                .put(path)
                .then()
                .log().all();
    }
    protected <T> ValidatableResponse doPatch(String path, String refreshToken, String accessToken, T requestBody) {

        RequestSpecification requestSpec = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        Optional.ofNullable(refreshToken).ifPresent(token -> requestSpec.header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + token));
        Optional.ofNullable(accessToken).ifPresent(token -> requestSpec.header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + token));
        Optional.ofNullable(requestBody).ifPresent(requestSpec::body);

        return requestSpec
                .when()
                .patch(path)
                .then()
                .log().all();
    }
    protected void removeStore(){
        doDelete(STORE_URL+"/"+storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken());
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
        memberResponse = convert(
                doPost(OWNER_URL, null,null,OWNER_SAVE_REQUEST)
                        .extract().as(com.twogather.twogatherwebbackend.dto.Response.class), new TypeReference<MemberResponse>(){}
                        );
        loginMemberId = memberResponse.getMemberId();
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
        registerKeyword();

        storeId = convert(doPost(STORE_URL,ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(), createStoreRequest(keywordList, categoryId))
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(com.twogather.twogatherwebbackend.dto.Response.class),  new TypeReference<StoreSaveUpdateResponse>() {}).getStoreId();
        registerBusinessHour(storeId, BUSINESS_HOUR_SAVE_UPDATE_REQUEST_LIST);
        registerMenu(storeId, MENU_SAVE_LIST_REQUEST);
        registerImage(storeId);

    }
    protected void registerStoreWithValidatorFail(){
        Long categoryId = registerCategory();
        registerKeyword();

        doPost(STORE_URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                createStoreRequest(keywordList, categoryId))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(StoreException.StoreErrorCode.BIZ_REG_NUMBER_VALIDATION.getMessage()));

    }
    private void registerBusinessHour(Long storeId, BusinessHourSaveUpdateListRequest request){
        String url = "/api/stores/"+ storeId +"/business-hours";
        doPost(url, ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(), request)
                .statusCode(HttpStatus.CREATED.value());
    }
    private void registerMenu(Long storeId, MenuSaveListRequest request){
        String url = "/api/stores/"+ storeId +"/menus";
        doPost(url, ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(), request)
                .statusCode(HttpStatus.CREATED.value());
    }
    public StoreSaveUpdateRequest createStoreRequest(List<Keyword> keywordList, Long categoryId){
        List<Long> keywordIdList = keywordList.stream().map(Keyword::getKeywordId).collect(Collectors.toList());
        return new StoreSaveUpdateRequest(STORE_NAME, STORE_ADDRESS, STORE_PHONE, "0000000000", "홍길동", LocalDate.now(),  keywordIdList,categoryId);
    }
    protected void registerKeyword(){
        keywordList = new ArrayList<>();
        Keyword keyword1 = keywordRepository.save(new Keyword("맛있는"));
        Keyword keyword2 = keywordRepository.save(new Keyword("분위기있는"));
        Keyword keyword3 = keywordRepository.save(new Keyword("청결한"));
        keywordList.add(keyword1);
        keywordList.add(keyword2);
        keywordList.add(keyword3);
    }
    protected void registerImage(Long storeId){
        String url = "/api/stores/"+ storeId +"/images";
        List<File> fileList =  createMockFiles();
        given()
                .multiPart("storeImageList", fileList.get(0))
                .multiPart("storeImageList", fileList.get(1))
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getRefreshToken())
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + ownerToken.getAccessToken())
                .when()
                .post(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    protected Long registerCategory(){
        Category category = categoryRepository.save(new Category("기타"));
        categoryId = category.getCategoryId();
        return category.getCategoryId();
    }
    protected void approveStore(){
        log.info("approve store");
        consumerRepository.save(ADMIN);
        adminToken = doLogin(ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/approve/" + storeId;
        doPatch(approveStoreUrl, adminToken.getRefreshToken(), adminToken.getAccessToken(),null)
                .statusCode(HttpStatus.OK.value());
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
    protected String generateExpiredToken() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 3600000);  // set the expiry date to 1 hour before now

        return JWT.create()
                .withSubject("Test User")
                .withExpiresAt(expiryDate)   // expiry date is in the past
                .withIssuedAt(now)
                .withClaim("id", 123)   // replace this with actual user's id
                .withClaim("role", "ROLE_CONSUMER")   // replace this with actual user's role
                .sign(Algorithm.HMAC512(constants.JWT_SECRET));

    }
    private List<File> createMockFiles() {
        List<File> fileList = new ArrayList<>();

        File multipartFile = new File("src\\test\\resources\\files\\image.jpg");

        fileList.add(multipartFile);
        fileList.add(multipartFile);

        return fileList;
    }
}
