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
import java.time.LocalDateTime;
import java.util.List;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.util.TestUtil.convert;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AdminAcceptanceTest  extends AcceptanceTest{
    @Autowired
    private StoreOwnerRepository ownerRepository;

    @BeforeEach
    public void init(){
        super.setUp();
        registerOwner();
        registerStoreWithFullInfo();
    }
    static final String REASON = "조건 불충족";

    @Test
    @DisplayName("가게 승인이 제대로 된경우 데이터베이스에도 승인이라는 정보가 남겨져있어야한다")
    public void whenApproveStore_ThenSuccess() {
        // when
        approveStore();

        //then
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(), StoreStatus.APPROVED);
    }

    @Test
    @DisplayName("가게 거부 제대로 되었는지 데이터베이스를 조회해서 상태와 거부이유를 확인해본다")
    public void whenRejectStore_ThenSuccess() {
        // given,when
        rejectStore();

        //then
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(), StoreStatus.DENIED);
        Assertions.assertEquals(storeRepository.findById(storeId).get().getReasonForRejection(), REASON);
    }


    @Test
    @DisplayName("승인 타입에 대한 가게목록을 불러와서 예상한 값과 일치하는지 확인한다")
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
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게1") &&
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게2") &&
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게3") &&
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertTrue(list.stream().anyMatch(e ->
                e.getStoreName().equals("가게4") &&
                        e.getStatus().equals(StoreStatus.APPROVED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));
        Assertions.assertFalse(list.stream().anyMatch(e ->
                e.getStoreName().equals("안승인된 가게") &&
                        e.getStatus().equals(StoreStatus.DENIED) &&
                        e.getPhone().equals(STORE_PHONE) &&
                        e.getAddress().equals(STORE_ADDRESS)));

    }

    @Test
    @DisplayName("승인된 가게 목록을 페이징이 잘 적용돼서 원하는 개수의 데이터만 전달해 오는지 확인해본다.")
    public void whenApprovedStorePagingWorking_ThenSuccess() {
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
    @DisplayName("승인된 가게목록을 불러올때 이미지도 불러올 수 있어야한다")
    public void whenCanFindApprovedStoreWithImage_ThenSuccess() {
        // given
        approveStore();

        String url = "/api/admin/stores/" + StoreStatus.APPROVED;

        //when
        int page = 0;
        int size = 2;
        given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + adminToken.getAccessToken())
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + adminToken.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("page", page)
                .param("size", size)
                .when()
                .get(url)
                .then()
                .log().all()
                .body("currentPage", equalTo(page))
                .body("totalPages", equalTo(1))
                .body("totalElements", equalTo(1))
                .body("pageSize", equalTo(size))
                .body("isFirst", equalTo(true))
                .body("isLast", equalTo(true))
                .body("data.storeImageUrl", not(emptyOrNullString()))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("거부된 가게 목록을 페이징이 잘 적용돼서 원하는 개수의 데이터만 전달해 오는지 확인해본다.")
    public void whenRejectStorePagingWorking_ThenSuccess() {
        // given
        adminLogin();
        saveDeniedStore();

        String url = "/api/admin/stores/" + StoreStatus.DENIED;

        //when
        int page = 0;
        int size = 2;
        given()
                .header(constants.REFRESH_TOKEN_HEADER, constants.TOKEN_PREFIX + adminToken.getAccessToken())
                .header(constants.ACCESS_TOKEN_HEADER, constants.TOKEN_PREFIX + adminToken.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("page", page)
                .param("size", size)
                .when()
                .get(url)
                .then()
                .log().all()
                .body("currentPage", equalTo(page))
                .body("totalPages", equalTo(1))
                .body("totalElements", equalTo(1))
                .body("pageSize", equalTo(size))
                .body("isFirst", equalTo(true))
                .body("isLast", equalTo(true))
                .statusCode(HttpStatus.OK.value());

    }
    @Test
    @DisplayName("관리자가 아닌 회원이 요청할떄 예외가 발생해야한다")
    public void whenNoAuth_ThenThrowException(){
        //given
        registerConsumer();
        String url = "/api/admin/stores/" + StoreStatus.DENIED;
        //when,then
        doGet(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken())
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
    @Test
    @DisplayName("가게 승인 요청 -> 거부 -> 재요청의 시나리오의 경우 데이터베이스에서 재요청상태/신청날짜를 확인할 수 있다")
    public void whenReapplyRequest_ThenSuccess() {
        // given
        rejectStore();
        //when
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(),StoreStatus.DENIED);

        doPatch("/api/stores/" + storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                null);
        //then
        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(),StoreStatus.PENDING);
//        Assertions.assertEquals(storeRepository.findById(storeId).get().getRequestDate(),LocalDate.now());

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
                .body("currentPage", equalTo(page))
                .body("totalPages", equalTo(3))
                .body("totalElements", equalTo(5))
                .body("pageSize", equalTo(size))
                .body("isFirst", equalTo(false))
                .body("isLast", equalTo(true))
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
                .reasonForRejection("요구사항 불충족")
                .requestDate(LocalDateTime.now())
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
