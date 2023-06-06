package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateRequest;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static com.twogather.twogatherwebbackend.TestConstants.STORE_SAVE_REQUEST;
import static com.twogather.twogatherwebbackend.TestConstants.STORE_URL;
import static org.hamcrest.Matchers.equalTo;


public class StoreExcludeGetAcceptanceTest extends AcceptanceTest{

    @Autowired
    private StoreRepository storeRepository;

    private static final String URL = "/api/stores";

    @BeforeEach
    public void init(){
        super.setUp();
        registerOwner();
    }

    @Test
    @DisplayName("가게 저장 성공")
    public void whenSaveValidStore_ThenReturnStoreInfo() {
        //when
        registerStore();
        approveStore();
        //then
        Assertions.assertTrue(storeRepository.findById(storeId).isPresent());
    }

    @Test
    @DisplayName("가게 저장 실패 - 사업자 등록번호 유효성 실패")
    public void whenSaveInValidBizRegNumber_ThenThrowException() {
        //when, then
        doPost(STORE_URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                STORE_SAVE_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    @DisplayName("가게 저장시 빈값, null, 올바르지않은 전화번호 형식에 대한 예외 throw")
    public void whenSaveIncludeNullFieldStore_ThenThrowsException()  {
        //given
        StoreSaveUpdateRequest invalidRequest = new StoreSaveUpdateRequest(
                null, "", "01012312312","0000000000", "홍길동", LocalDate.now()
        );
        //when, then
        doPost(STORE_URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                invalidRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지않은 값을 입력하였습니다"));

    }

    @Test
    @DisplayName("유효한 값으로 가게 업데이트 성공 및 확인 test")
    public void whenUpdateValidStore_ThenReturnStoreInfo()  {
        //given
        registerStore();
        approveStore();
        StoreSaveUpdateRequest updateRequest = new StoreSaveUpdateRequest(
                "updateName", "updateAddress", "063-231-4999",
                "0000000001", "홍길당", LocalDate.now()
        );
        //when, then
        doPut(URL+"/"+storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                updateRequest).statusCode(HttpStatus.OK.value());


    }

    @Test
    @DisplayName("유효하지않은 값(null field)으로 가게 업데이트 시도 시 실패하면서 롤백되는 test")
    public void whenUpdateStoreIncludeNullField_ThenThrowException()  {
        //given
        registerStore();
        approveStore();
        StoreSaveUpdateRequest updateRequest = new StoreSaveUpdateRequest(
                null, "updateAddress", "063-231-4999",
                "0000000001", "홍길당", LocalDate.now()
        );
        //when, then
        doPut(URL+"/"+storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                updateRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지않은 값을 입력하였습니다"));;
    }

    @Test
    @DisplayName("유효하지않은 전화번호로 가게 업데이트 시도 시 실패하면서 롤백되는 test")
    public void whenUpdateStoreIncludePhoneField_ThenThrowException() {
        //given
        registerStore();
        approveStore();
        StoreSaveUpdateRequest updateRequest = new StoreSaveUpdateRequest(
                null, "updateAddress", "061233-231-4999",
                "0000000001", "홍길당", LocalDate.now()
        );
        //when, then
        doPut(URL+"/"+storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                updateRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지않은 값을 입력하였습니다"));;
    }


    @Test
    @DisplayName("가게 삭제 요청 후 진짜 삭제되었는지 확인해보는 test")
    public void whenDeleteStore_ThenNotExistStore() {
        //given
        registerStore();
        approveStore();

        //when,then
        doDelete(URL+"/"+storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken()).statusCode(HttpStatus.OK.value());

        Assertions.assertEquals(storeRepository.findById(storeId).get().getStatus(), StoreStatus.DELETED);
    }
    @Test
    @DisplayName("오직 자신의 가게만 삭제할 수 있다")
    public void whenDeleteNotMyStore_ThenThrowException()  {
        //given
        Long noSuchId = 131232l;
        //when,then
        doDelete(URL+"/"+noSuchId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken())
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
