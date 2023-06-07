package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.dto.PagedResponse;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.store.MyStoreResponse;
import com.twogather.twogatherwebbackend.dto.store.RejectReason;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.TestUtil.convert;
import static io.restassured.RestAssured.given;

public class AdminAcceptanceTest  extends AcceptanceTest{
    @BeforeEach
    public void init(){
        super.setUp();
        registerOwner();
        registerStore();
    }

    static final String REASON = "조건 불충족";
    @Test
    @DisplayName("가게 승인이 제대로 되었는지 확인")
    public void whenApproveStore_ThenSuccess() {
        // when
        approveStore();

        //then
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(), StoreStatus.APPROVED);
    }

    @Test
    @DisplayName("가게 거부 제대로 되었는지 확인")
    public void whenRejectStore_ThenSuccess() {
        // given,when
        rejectStore();

        //then
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(), StoreStatus.DENIED);
        Assertions.assertEquals(storeRepository.findById(storeId).get().getReasonForRejection(), REASON);
    }


    @Test
    @DisplayName("승인된 가게 목록 찾아오기")
    public void whenFindApprovedStore_ThenSuccess() {
        // given
        adminLogin();
        String url = "/api/admin/stores/" + StoreStatus.APPROVED;
        saveStore();

        //when
        List<MyStoreResponse> list = convert(doGet(url,
                adminToken.getRefreshToken(),
                adminToken.getAccessToken())
                .extract().as(PagedResponse.class), new TypeReference<List<MyStoreResponse>>() {});

        //then
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게0") &&
                        e.getIsApproved().equals(true) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게1") &&
                        e.getIsApproved().equals(true) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게2") &&
                        e.getIsApproved().equals(true) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게3") &&
                        e.getIsApproved().equals(true) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게4") &&
                        e.getIsApproved().equals(true) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertFalse(list.stream().anyMatch(e ->
                e.getStoreName().equals("안승인된 가게") &&
                        e.getIsApproved().equals(true) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));

    }

    @Test
    @DisplayName("승인된 가게 목록 찾아오기 - 페이징이 잘 적용되는지")
    public void whenPagingWorking_ThenSuccess() {
        // given
        adminLogin();
        saveStore();

        String url = "/api/admin/stores/" + StoreStatus.APPROVED;

        //when
        List<MyStoreResponse> list = getStoreList(url);
        //then
        Assertions.assertEquals(list.size(),1);
    }

    @Test
    @DisplayName("승인요청 -> 거부 -> 재요청")
    public void whenReapplyRequest_ThenSuccess() {
        // given
        adminLogin();
        saveStore();
        rejectStore();
        //when
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(),StoreStatus.DENIED);

        doPatch("/api/stores/" + storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken());
        //then
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(),StoreStatus.PENDING);
    }

    private List<MyStoreResponse> getStoreList(String url){
        int page = 2;
        int size = 2;
        return convert(given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + adminToken.getAccessToken())
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + adminToken.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("page", page)
                .param("size", size)
                .when()
                .get(url)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(PagedResponse.class), new TypeReference<List<MyStoreResponse>>() {});

    }
    private void saveStore(){
        for(int i=0;i<5;i++){
            Store store = Store.builder()
                    .name("가게" + i)
                    .address(STORE_ADDRESS)
                    .phone(STORE_PHONE)
                    .status(StoreStatus.APPROVED).build();
            storeRepository.save(store);
        }
        Store deniedStore = Store.builder()
                .name("안승인된 가게")
                .address(STORE_ADDRESS)
                .phone(STORE_PHONE)
                .status(StoreStatus.DENIED).build();
        storeRepository.save(deniedStore);
    }
    private void rejectStore(){
        adminLogin();
        String url = "/api/admin/stores/reject/" + storeId;

        doPatch(url,
                adminToken.getRefreshToken(),
                adminToken.getAccessToken(),
                new RejectReason(REASON));
    }
    private void adminLogin(){
        consumerRepository.save(ADMIN);
        adminToken = doLogin(ADMIN_LOGIN_REQUEST);
    }

}
