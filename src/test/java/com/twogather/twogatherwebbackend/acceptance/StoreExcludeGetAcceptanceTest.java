package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateListRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateResponse;
import com.twogather.twogatherwebbackend.repository.BusinessHourRepository;
import com.twogather.twogatherwebbackend.repository.ImageRepository;
import com.twogather.twogatherwebbackend.repository.MenuRepository;
import com.twogather.twogatherwebbackend.repository.StoreKeywordRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.DUPLICATE_NAME;
import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;
import static com.twogather.twogatherwebbackend.util.TestConstants.*;

import static com.twogather.twogatherwebbackend.util.TestUtil.convert;
import static org.hamcrest.Matchers.*;


public class StoreExcludeGetAcceptanceTest extends AcceptanceTest{

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private BusinessHourRepository businessHourRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private StoreKeywordRepository storeKeywordRepository;

    private static final String URL = "/api/stores";

    @BeforeEach
    public void init(){
        super.setUp();
        registerOwner();
    }

    @Test
    @DisplayName("가게의 기본적인 정보를 저장하는데 제약사항을 만족했다면 성공해야한다")
    public void whenSaveValidStore_ThenReturnStoreInfo() {
        //when
        registerStore();
        approveStore();

        //then
        Store store = storeRepository.findById(storeId).get();
        Assertions.assertNotNull(store);
        Assertions.assertTrue(categoryRepository.findById(store.getCategory().getCategoryId()).isPresent());
        Assertions.assertTrue(!businessHourRepository.findByStoreStoreId(storeId).isEmpty());
        Assertions.assertTrue(businessHourRepository.findByStoreStoreId(storeId).size() == 7);
        Assertions.assertTrue(!menuRepository.findByStoreStoreId(storeId).isEmpty());
        Assertions.assertTrue(!storeKeywordRepository.findByStoreStoreId(storeId).isEmpty());
    }

    @Test
    @DisplayName("유효하지않은 사업자등록번호정보로 저장은 실패해야한다")
    public void whenSaveInvalidBusinessInfo_ThenThrowException() {
        //when,then
        registerStoreWithValidatorFail();

    }

    @Test
    @DisplayName("가게 저장시 빈값, null, 올바르지않은 전화번호 형식에 대한 예외 throw")
    public void whenSaveIncludeNullFieldStore_ThenThrowsException()  {
        //given
        StoreSaveUpdateRequest invalidRequest = new StoreSaveUpdateRequest(
                null, "", "01012312312","0000000000", "홍길동", LocalDate.now(),
                new ArrayList<>(), 1l
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
    @DisplayName("유효한 값으로 가게 업데이트 시 데이터베이스를 조회해서 제대로 업데이트가 되었는지 값일치를 확인해봤을때 일치해야한다")
    public void whenUpdateValidStore_ThenReturnStoreInfo()  {
        //given
        registerStore();
        approveStore();
        StoreSaveUpdateRequest updateRequest = new StoreSaveUpdateRequest(
                "updateName", "updateAddress", "063-231-4999",
                "0000000001", "홍길당", LocalDate.now(), new ArrayList<>(),1l
        );
        //when, then
        doPut(URL+"/"+storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                updateRequest).statusCode(HttpStatus.OK.value());

        Store store = storeRepository.findById(storeId).get();
        Assertions.assertEquals(store.getName(), updateRequest.getStoreName());
        Assertions.assertEquals(store.getAddress(), updateRequest.getAddress());
        Assertions.assertEquals(store.getPhone(), updateRequest.getPhone());
        Assertions.assertEquals(store.getBusinessName(), updateRequest.getBusinessName());
        Assertions.assertEquals(store.getBusinessNumber(), updateRequest.getBusinessNumber());
        Assertions.assertEquals(store.getBusinessStartDate(), updateRequest.getBusinessStartDate());

    }

    @Test
    @DisplayName("유효하지않은 값(null field)으로 가게 업데이트 시도 시 실패하면서 롤백돼야한다")
    public void whenUpdateStoreIncludeNullField_ThenThrowException()  {
        //given
        registerStore();
        approveStore();
        StoreSaveUpdateRequest updateRequest = new StoreSaveUpdateRequest(
                null, "updateAddress", "063-231-4999",
                "0000000001", "홍길당", LocalDate.now(), null,1l
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
    @DisplayName("유효하지않은 전화번호로 가게 업데이트 시도 시 실패하면서 롤백돼야한다 ")
    public void whenUpdateStoreIncludePhoneField_ThenThrowException() {
        //given
        registerStore();
        approveStore();
        StoreSaveUpdateRequest updateRequest = new StoreSaveUpdateRequest(
                null, "updateAddress", "061233-231-4999",
                "0000000001", "홍길당", LocalDate.now(), null, 1l
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
    @DisplayName("똑같은 가게 이름으로 두번 등록시 4xx error가 터진다")
    public void whenRegisterSameStoreName_ThenThrowException() {
        //given
        registerStore();
        //when
        registerStoreDuplicate();
    }

    @Test
    @DisplayName("가게 삭제 요청 후 가게 관련 요소(메뉴, 영업시간, 이미지)도 다 삭제되어야한다")
    public void whenDeleteStore_ThenNotExistStore() {
        //given
        registerStore();
        approveStore();

        Assertions.assertFalse(businessHourRepository.findAll().isEmpty());
        Assertions.assertFalse(menuRepository.findAll().isEmpty());
        Assertions.assertFalse(storeKeywordRepository.findAll().isEmpty());
        Assertions.assertFalse(imageRepository.findAll().isEmpty());

        //when
        doDelete(URL+"/"+storeId,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken()).statusCode(HttpStatus.OK.value());

        //then
        Assertions.assertFalse(storeRepository.findById(storeId).isPresent());
        Assertions.assertTrue(businessHourRepository.findAll().isEmpty());
        Assertions.assertTrue(menuRepository.findAll().isEmpty());
        Assertions.assertTrue(storeKeywordRepository.findAll().isEmpty());
        Assertions.assertTrue(imageRepository.findAll().isEmpty());
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
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo(NO_SUCH_STORE.getMessage()));
    }

    public void registerStoreDuplicate() {
        validatorWillPass();

        doPost(STORE_URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                createStoreRequest(keywordList, categoryId))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(DUPLICATE_NAME.getMessage()));


    }
}
