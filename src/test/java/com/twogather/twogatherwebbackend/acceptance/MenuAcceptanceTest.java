package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twogather.twogatherwebbackend.controller.MenuController;
import com.twogather.twogatherwebbackend.domain.Menu;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.menu.MenuResponse;
import com.twogather.twogatherwebbackend.dto.menu.MenuSaveRequest;
import com.twogather.twogatherwebbackend.dto.menu.MenuUpdateRequest;
import com.twogather.twogatherwebbackend.repository.MenuRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.service.MenuService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.TestUtil.convert;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.*;

public class MenuAcceptanceTest extends AcceptanceTest{

    private String URL;

    @BeforeEach
    public void setup(){
        super.setUp();
        registerOwner();
        registerStore();
        approveStore();
        URL = "/api/stores/" + storeId + "/menus";
    }

    @Test
    @DisplayName("Save menu 성공")
    public void saveMenuList_WithValidMenuList_ThenMenuSaved(){
        // Given
        // When, Then
        doPost(URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                MENU_SAVE_LIST_REQUEST)
                .statusCode(HttpStatus.CREATED.value())
                .body("data.findAll { it.name == '감자' && it.price == 1000 }", hasSize(greaterThanOrEqualTo(1)))
                .body("data.findAll { it.name == '케찹' && it.price == 2000 }", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("Save menu 시에 유효성 실패 - null 입력하면 안됨")
    public void saveMenuList_WithInputNull_ThenThrowException(){
        // Given
        // When, Then
        doPost(URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                MENU_SAVE_LIST_MINUS_VALUE_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지않은 값을 입력하였습니다"));

    }

    @Test
    @DisplayName("Save menu 시에 유효성 실패 - 음수 가격 입력하면 안됨")
    public void saveMenuList_WithInputMinusPrice_ThenThrowException()  {
        // Given
        // When, Then

        doPost(URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                MENU_SAVE_LIST_MINUS_VALUE_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지않은 값을 입력하였습니다"));
    }

    @Test
    @DisplayName("update menu 성공")
    public void updateMenuList_WithValidMenuList_ThenMenuSaved()  {
        // given,when
        createMenu();

        // Then
        doPatch(URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                MENU_UPDATE_LIST_REQUEST)
                .statusCode(HttpStatus.OK.value())
                .body("data.findAll { it.name == 'new 감자' && it.price == 10000 }", hasSize(greaterThanOrEqualTo(1)))
                .body("data.findAll { it.name == 'new 케찹' && it.price == 20000 }", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("update menu 시에 유효성 실패 - null 입력하면 안됨")
    public void updateMenuList_WithInputNull_ThenThrowException() {
        // Given
        // When, Then
        doPatch(URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                MENU_UPDATE_LIST_NULL_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지않은 값을 입력하였습니다"));
    }

    @Test
    @DisplayName("update menu 시에 유효성 실패 - 음수 가격 입력하면 안됨")
    public void updateMenuList_WithInputMinusPrice_ThenThrowException(){
        // Given
        // When, Then
        doPatch(URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                MENU_UPDATE_LIST_MINUS_VALUE_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("유효하지않은 값을 입력하였습니다"));
    }

    private List<MenuResponse> createMenu(){
        List<MenuResponse> response = convert(doPost(URL, ownerToken.getRefreshToken(), ownerToken.getAccessToken(), MENU_SAVE_LIST_REQUEST)
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(Response.class), new TypeReference<>(){});
        return response;

    }
}
