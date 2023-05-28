package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.category.CategoryResponse;
import com.twogather.twogatherwebbackend.repository.CategoryRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.service.CategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.acceptance.TestHelper.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryAcceptanceTest {
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
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StoreRepository storeRepository;

    @Test
    @Transactional
    public void whenGetAllCategories_ThenAllCategoryList() throws Exception {
        Category category1 = new Category("category1");
        Category category2 = new Category("category2");
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);
        categoryRepository.saveAll(categoryList);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value(category1.getName()))
                .andExpect(jsonPath("$.data[1].name").value(category2.getName()))
                .andExpect(jsonPath("$.data[0].categoryId").exists())
                .andExpect(jsonPath("$.data[1].categoryId").exists())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @Transactional
    public void whenSetCategoriesForStore_ThenAssociateCategoryWithStore() throws Exception {
        //given
        StoreOwner owner = createOwner(ownerRepository, passwordEncoder);
        Store store = createStore(storeRepository,owner);

        Category category = categoryRepository.save(new Category("categoryName"));
        Long storeId = store.getStoreId();
        Long categoryId = category.getCategoryId();

        createAuthority(owner);

        String url = "/api/stores/" + storeId + "/categories/" + categoryId;
        mockMvc.perform(patch(url))
                .andExpect(status().isOk());
        categoryService.setCategoriesForStore(storeId, categoryId);

        //when, then

        em.flush();
        em.clear();
        Category savedCategory = storeRepository.findById(store.getStoreId()).get().getCategory();
        Assertions.assertEquals(savedCategory.getName(), category.getName());
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
        mockMvc.perform(patch(url))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

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
        mockMvc.perform(patch(url))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

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
        mockMvc.perform(patch(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("해당하는 카테고리가 존재하지않습니다"))
                .andDo(MockMvcResultHandlers.print());

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
        mockMvc.perform(patch(url))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("권한이 없습니다"))
                .andDo(MockMvcResultHandlers.print());

        em.flush();
        em.clear();
        Category categoryOfStore1  = storeRepository.findById(store1.getStoreId()).get().getCategory();
        Assertions.assertNull(categoryOfStore1);



    }
}
