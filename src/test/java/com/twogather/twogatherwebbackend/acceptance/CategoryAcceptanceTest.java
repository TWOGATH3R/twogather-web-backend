package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.Tokens;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.member.MemberResponse;
import com.twogather.twogatherwebbackend.dto.store.StoreResponse;
import com.twogather.twogatherwebbackend.repository.CategoryRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.service.CategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.acceptance.TestHelper.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CategoryAcceptanceTest extends AcceptanceTest{
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StoreOwnerRepository ownerRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EntityManager em;
    @Autowired
    private StoreRepository storeRepository;

    private Category category1;
    private Category category2;

    public void initSetting(){
        category1 = new Category("category1");
        category2 = new Category("category2");
        category1 = categoryRepository.save(category1);
        category2 = categoryRepository.save(category2);
    }

    /*
    @Test
    public void whenGetAllCategories_ThenAllCategoryList(){
        initSetting();

        when().
                get(CATEGORY_URL).
                then().
                statusCode(HttpStatus.OK.value()).
                log().all().
                body("data[0].name", equalTo(category1.getName())).
                body("data[1].name", equalTo(category2.getName())).
                body("data[0].categoryId", notNullValue()).
                body("data[1].categoryId", notNullValue());
    }


    @Test
    public void whenSetCategoriesForStore_ThenAssociateCategoryWithStore() {
        //given
        doPost(OWNER_URL, OWNER_SAVE_UPDATE_REQUEST, MemberResponse.class);
        Tokens myToken = doLogin(LOGIN_URL, OWNER_LOGIN_REQUEST);

        validatorWillPass();
        StoreResponse storeResponse =
                doPost(STORE_URL,
                        myToken.getRefreshToken(),
                        myToken.getAccessToken(),
                        STORE_SAVE_REQUEST,
                        StoreResponse.class);

        Long storeId = storeResponse.getStoreId();
        initSetting();
        Long categoryId = category1.getCategoryId();

        //when, then
        String url = "/api/stores/" + storeId + "/categories/" + categoryId;
        when().
                patch(url).
                then().
                statusCode(HttpStatus.OK.value()).
                log().all();

        em.flush();
        em.clear();

        Category savedCategory = storeRepository.findById(storeId).get().getCategory();
        Assertions.assertEquals(savedCategory.getName(), category1.getName());
    }

    @Test
    @Transactional
    @DisplayName("이미 카테고리가 설정된 가게의 카테고리를 다른걸로 다시 설정했을때 제대로 반영됐는지 확인한다")
    public void whenUpdateCategory_thenUpdatedCategoryIsReturned() throws Exception {
        //given
        StoreOwner owner = createOwner(ownerRepository, passwordEncoder);
        Store store = createStore(storeRepository,owner);

        Category originCategory = categoryRepository.save(new Category("categoryName"));
        Category newCategory = categoryRepository.save(new Category("newCategoryName"));
        Long storeId = store.getStoreId();
        Long categoryId = newCategory.getCategoryId();
        store.setCategory(originCategory);//set category

        createAuthority(owner);

        String url = "/api/stores/" + storeId + "/categories/" + categoryId;
        when().
                patch(url).
                then().
                statusCode(400).
                body("message", equalTo("해당하는 카테고리가 존재하지않습니다")).
                log().all();

        em.flush();
        em.clear();
        Category savedCategory = storeRepository.findById(store.getStoreId()).get().getCategory();
        Assertions.assertEquals(savedCategory.getName(), newCategory.getName());
    }

    @Test
    @Transactional
    @DisplayName("똑같은 카테고리로 서로 다른 가게에 대해 두번 setting 요청을 보냈을때 에러가 터지지 않는다")
    public void whenSecondStoreRequestsSameCategory_thenNoErrorIsThrown() throws Exception {
        //given
        StoreOwner owner = createOwner(ownerRepository, passwordEncoder);
        Store store1 = createStore(storeRepository,owner);
        Store store2 = createStore(storeRepository,owner);

        Category category = categoryRepository.save(new Category("categoryName"));
        Long store2Id = store2.getStoreId();
        Long categoryId = category.getCategoryId();
        store1.setCategory(category);

        createAuthority(owner);

        String url = "/api/stores/" + store2Id + "/categories/" + categoryId;
        when().
                patch(url).
                then().
                statusCode(403).
                body("message", equalTo("권한이 없습니다")).
                log().all();

        em.flush();
        em.clear();
        Category categoryOfStore1  = storeRepository.findById(store1.getStoreId()).get().getCategory();
        Category categoryOfStore2 = storeRepository.findById(store2.getStoreId()).get().getCategory();
        Assertions.assertEquals(categoryOfStore1, categoryOfStore2);


    }

    @Test
    @Transactional
    @DisplayName("없는 카테고리로 등록시 throw exception")
    public void whenNoSuchCategory_thenThrowException() throws Exception {
        //given
        StoreOwner owner = createOwner(ownerRepository, passwordEncoder);
        Store store1 = createStore(storeRepository,owner);
        Store store2 = createStore(storeRepository,owner);

        Long noSuchCategoryId = 123l;
        createAuthority(owner);

        String url = "/api/stores/" + store2.getStoreId() + "/categories/" + noSuchCategoryId;
        when().
                patch(url).
                then().
                statusCode(400).
                body("message", equalTo("해당하는 카테고리가 존재하지않습니다")).
                log().all();

        em.flush();
        em.clear();
        Category categoryOfStore1  = storeRepository.findById(store1.getStoreId()).get().getCategory();
        Category categoryOfStore2 = storeRepository.findById(store2.getStoreId()).get().getCategory();
        Assertions.assertEquals(categoryOfStore1, categoryOfStore2);

    }

    @Test
    @Transactional
    @DisplayName("자신의 가게가 아닌 가게id로 카테고리를 등록시 throw exception")
    public void whenNoSuchStore_ThenThrowException() throws Exception {
        //given
        StoreOwner owner = createOwner(ownerRepository, passwordEncoder);
        Store store1 = createStore(storeRepository,owner);

        Category category = categoryRepository.save(new Category("categoryName"));

        Long categoryId = category.getCategoryId();

        Long noSuchStoreId = 123l;
        createAuthority(owner);

        String url = "/api/stores/" + noSuchStoreId + "/categories/" + categoryId;
        when().
                patch(url).
                then().
                statusCode(403).
                body("message", equalTo("권한이 없습니다")).
                log().all();

        em.flush();
        em.clear();
        Category categoryOfStore1  = storeRepository.findById(store1.getStoreId()).get().getCategory();
        Assertions.assertNull(categoryOfStore1);



    }*/
}
