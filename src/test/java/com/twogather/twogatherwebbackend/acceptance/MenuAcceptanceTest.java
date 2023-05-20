package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.controller.MenuController;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.menu.MenuResponse;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveRequest;
import com.twogather.twogatherwebbackend.repository.MenuRepository;
import com.twogather.twogatherwebbackend.repository.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;

import static com.twogather.twogatherwebbackend.acceptance.TestHelper.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class MenuAcceptanceTest {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private StoreOwnerRepository ownerRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MenuService menuService;
    @Autowired
    private EntityManager em;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private StoreOwner owner;
    private Store store;
    private static final String URL = "/api/stores/{storeId}/menus";

    @BeforeEach
    public void setup(){
        owner = createOwner(ownerRepository, passwordEncoder);
        store = createStore(storeRepository,owner);
        createAuthority(owner);
    }

    @Test
    @DisplayName("Save menu 성공")
    public void saveMenuList_WithValidMenuList_ThenMenuSaved() throws Exception {
        // Given
        List<MenuSaveRequest> menuSaveList = Arrays.asList(
                new MenuSaveRequest("감자", 1000),
                new MenuSaveRequest("케찹", 2000)
        );
        MenuController.MenuSaveListRequest request = new MenuController.MenuSaveListRequest(menuSaveList);

        // When, Then
        mockMvc.perform(post(URL, store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data[?(@.name == '감자' && @.price == 1000)]").exists())
                .andExpect(jsonPath("$.data[?(@.name == '케찹' && @.price == 2000)]").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Save menu 시에 유효성 실패 - null 입력하면 안됨")
    public void saveMenuList_WithInputNull_ThenThrowException() throws Exception {
        // Given
        List<MenuSaveRequest> menuSaveList = Arrays.asList(
                new MenuSaveRequest(null, 1000), // Name is null (violation)
                new MenuSaveRequest("케찹", null) // Price is null (violation)
        );
        MenuController.MenuSaveListRequest request = new MenuController.MenuSaveListRequest(menuSaveList);

        // When, Then
        // No value at JSON path "$.menuSaveList[0].name" < jsonpath 문법때문에 인식못하는듯. 응답은 성공적으로 옴
        mockMvc.perform(post(URL, store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지않은 값을 입력하였습니다"))
                 .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Save menu 시에 유효성 실패 - 음수 가격 입력하면 안됨")
    public void saveMenuList_WithInputMinusPrice_ThenThrowException() throws Exception {
        // Given
        List<MenuSaveRequest> menuSaveList = Arrays.asList(
                new MenuSaveRequest(null, -1000), // Name is null (violation)
                new MenuSaveRequest("케찹", null) // Price is null (violation)
        );
        MenuController.MenuSaveListRequest request = new MenuController.MenuSaveListRequest(menuSaveList);

        // When, Then
        // No value at JSON path "$.menuSaveList[0].name" < jsonpath 문법때문에 인식못하는듯. 응답은 성공적으로 옴
        mockMvc.perform(post(URL, store.getStoreId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지않은 값을 입력하였습니다"))
                .andDo(MockMvcResultHandlers.print());
    }


}
