package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.repository.CategoryRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.twogather.twogatherwebbackend.exception.StoreException.StoreErrorCode.NO_SUCH_STORE;
import static org.hamcrest.Matchers.equalTo;

public class CategoryAcceptanceTest extends AcceptanceTest{
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StoreRepository storeRepository;

    private Category category1;
    private Category category2;

    @BeforeEach
    public void initSetting(){
        super.setUp();
        registerOwner();
        registerStoreWithFullInfo();
        approveStore();
        createCategory();
    }

    @Test
    @DisplayName("가게에 대해 카테고리를 설정하고 데이터베이스를 조회했을때 제대로 관계가 설정되어있어야한다")
    public void whenSetCategoriesForStore_ThenAssociateCategoryWithStore() {
        //given
        String url = "/api/stores/" + storeId + "/categories/" + category1.getCategoryId();
        //when
        doPatch(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(),null);

        //then
        Long savedCategoryId = storeRepository.findById(storeId).get().getCategory().getCategoryId();
        Assertions.assertEquals(savedCategoryId, category1.getCategoryId());
    }


    @Test
    @DisplayName("이미 카테고리가 설정된 가게의 카테고리를 다른걸로 다시 설정했을때 두번째 카테고리로 설정되어야 한다")
    public void whenUpdateCategory_thenUpdatedCategoryIsReturned()  {
        //given
        String url = "/api/stores/" + storeId + "/categories/" + category1.getCategoryId();
        settingStoreCategory(url);
        String url2 = "/api/stores/" + storeId + "/categories/" + category2.getCategoryId();
        settingStoreCategory(url2);

        Long savedCategoryId = storeRepository.findById(storeId).get().getCategory().getCategoryId();
        Assertions.assertEquals(savedCategoryId, category2.getCategoryId());
    }

    /*
    @Test
    @DisplayName("똑같은 카테고리로 서로 다른 가게에 대해 두번 setting 요청을 보냈을때 에러가 터지지 않는다")
    public void whenSecondStoreRequestsSameCategory_thenNoErrorIsThrown() {
        //given
        Long storeId2 = registerStore(STORE_SAVE_REQUEST2,null,null,null);

        approveStore2(storeId2);


        String url = "/api/stores/" + storeId + "/categories/" + category1.getCategoryId();
        settingStoreCategory(url);
        String url2 = "/api/stores/" + storeId2 + "/categories/" + category1.getCategoryId();
        settingStoreCategory(url2);

    }*/

    @Test
    @DisplayName("없는 카테고리로 가게를 등록시 throw exception")
    public void whenNoSuchCategory_thenThrowException() {
        //given
        Long noSuchId = 12313l;
        String url = "/api/stores/" + storeId + "/categories/" + noSuchId;
        settingStoreCategory(url)
                .statusCode(HttpStatus.NOT_FOUND.value());

        Assertions.assertTrue(storeRepository.findById(storeId).isPresent());
        Assertions.assertFalse(storeRepository.findById(noSuchId).isPresent());
    }

    @Test
    @DisplayName("자신의 가게가 아닌 가게 id로 카테고리를 등록시 throw exception")
    public void whenNoSuchStore_ThenThrowException(){
        //given
        Long noSuchStoreId = 123l;
        String url = "/api/stores/" + noSuchStoreId + "/categories/" + category1.getCategoryId();
        //when
        doPatch(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(),null)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message",equalTo(NO_SUCH_STORE.getMessage()));

    }
    private void createCategory(){
        category1 = categoryRepository.save(new Category("양식"));
        category2 = categoryRepository.save(new Category("일식"));
    }
    private ValidatableResponse settingStoreCategory(String url){
        return doPatch(url, ownerToken.getRefreshToken(), ownerToken.getAccessToken(),null);
    }
}
