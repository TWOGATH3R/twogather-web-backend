package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.dto.menu.MenuUpdateInfo;
import com.twogather.twogatherwebbackend.dto.menu.MenuUpdateListRequest;
import com.twogather.twogatherwebbackend.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


import java.util.ArrayList;
import java.util.Arrays;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.Matchers.*;

public class MenuAcceptanceTest extends AcceptanceTest{

    @Autowired
    private MenuRepository menuRepository;
    private String URL;

    @BeforeEach
    public void setup(){
        super.setUp();
        registerOwner();
        registerStoreWithFullInfo();//createMenu을 포함하고 있음
        approveStore();
        URL = "/api/stores/" + storeId + "/menus";
    }


    @Test
    @DisplayName("update menu시 제약사항을 만족했다면 성공해야한다")
    public void updateMenuList_WithValidMenuList_ThenMenuSaved()  {
        // given,when
        // Then

        doPatch(URL,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                createMenu())
                .statusCode(HttpStatus.OK.value())
                .body("data.findAll { it.name == 'new 감자' && it.price == 10000 }", hasSize(greaterThanOrEqualTo(1)))
                .body("data.findAll { it.name == 'new 케찹' && it.price == 20000 }", hasSize(greaterThanOrEqualTo(1)));

    }

    @Test
    @DisplayName("update menu 시에 null을 입력하면 유효성 실패해야한다")
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
    @DisplayName("update menu 시에 음수가격을 입력해서 유효성검사에 실패해야한다")
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
    private MenuUpdateListRequest createMenu(){
        Long menuId1 = menuRepository.findByStoreId(storeId).get(0).getMenuId();
        Long menuId2 = menuRepository.findByStoreId(storeId).get(1).getMenuId();
        ArrayList<MenuUpdateInfo> list = new ArrayList<>(Arrays.asList(
                new MenuUpdateInfo(menuId1, "new 감자", 10000),
                new MenuUpdateInfo(menuId2, "new 케찹", 20000)
        ));
        return new MenuUpdateListRequest(list);
    }

}
