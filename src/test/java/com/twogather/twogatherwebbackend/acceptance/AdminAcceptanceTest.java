package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.dto.PagedResponse;
import com.twogather.twogatherwebbackend.dto.store.MyStoreResponse;
import com.twogather.twogatherwebbackend.dto.store.RejectReason;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.util.TestUtil.convert;
import static io.restassured.RestAssured.given;

public class AdminAcceptanceTest  extends AcceptanceTest{

    @Autowired
    private StoreOwnerRepository ownerRepository;
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
        rejectStore();
        //when
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(),StoreStatus.DENIED);

        doPatch("/api/stores/" + storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken());
        //then
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(),StoreStatus.PENDING);
    }

    @Test
    @DisplayName("거부된 가게 재요청 시 날짜 업데이트")
    public void whenReapplyRequest_ThenUpdateDate() {
        // given
        adminLogin();

        Long storeId = saveDeniedStore();

        Assertions.assertEquals(storeRepository.findById(storeId).get().getRequestDate(),LocalDate.of(2020,2,2));
        //when
        doPatch("/api/stores/" + storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken());

        //then
        Assertions.assertEquals(storeRepository.findById(storeId).get().getRequestDate(),LocalDate.now());
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
    private Long saveDeniedStore(){
        StoreOwner owner = ownerRepository.findByUsername(OWNER_USERNAME).get();
        Store store = Store.builder()
                .name("가게")
                .owner(owner)
                .address(STORE_ADDRESS)
                .phone(STORE_PHONE)
                .status(StoreStatus.DENIED)
                .requestDate(LocalDate.of(2020,2,2))
                .build();
        return storeRepository.save(store).getStoreId();
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
