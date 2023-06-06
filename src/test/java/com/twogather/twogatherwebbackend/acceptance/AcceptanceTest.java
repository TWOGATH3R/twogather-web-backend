package com.twogather.twogatherwebbackend.acceptance;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.Tokens;
import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import com.twogather.twogatherwebbackend.dto.LoginResponse;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateRequest;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.TestUtil.convert;
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
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ConsumerRepository consumerRepository;
    @MockBean
    private BizRegNumberValidator validator;
    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if(RestAssured.port == UNDEFINED_PORT){
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
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
    protected void registerStore(){
        log.info("register store");
        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        ownerToken.getRefreshToken(),
                        ownerToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);

        storeId = storeResponse.getStoreId();
        System.out.println(storeId + ": myinfo");
    }
    protected Long registerStore(StoreSaveUpdateRequest request){
        log.info("register store");
        validatorWillPass();
        storeId = doPost(STORE_URL,
                        ownerToken.getRefreshToken(),
                        ownerToken.getAccessToken(),
                        request,
                        StoreResponse.class).getStoreId();
        return storeId;
    }
    protected void approveStore(){
        log.info("approve store");
        consumerRepository.save(ADMIN);
        adminToken = doLogin(ADMIN_LOGIN_REQUEST);
        String approveStoreUrl = "/api/admin/stores/" + storeId;
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

}
