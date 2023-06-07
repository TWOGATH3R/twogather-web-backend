package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.dto.menu.MenuIdList;
import com.twogather.twogatherwebbackend.service.BusinessHourService;
import com.twogather.twogatherwebbackend.service.MenuService;
import com.twogather.twogatherwebbackend.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getDayOfWeekFormat;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getTimeFormat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MenuController.class)
public class MenuControllerTest extends ControllerTest{
    @MockBean
    private MenuService menuService;
    @MockBean
    private StoreService storeService;
    private static final String URL = "/api/stores/{storeId}/menus";


    @Test
    @DisplayName("가게에 대한 모든 메뉴 받아오기")
    public void getMenuListByStore_WhenGetMenuList_ThenReturnMenuList() throws Exception {
        //given
        when(menuService.findMenusByStoreId(anyLong())).thenReturn(MENU_RESPONSE_LIST);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL,1)
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("menu/get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 ID")
                        ),
                        responseFields(
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("메뉴의 가격"),
                                fieldWithPath("data[].menuId").type(JsonFieldType.NUMBER).description("메뉴의 고유 id")

                        )
                ));

    }

    @Test
    @DisplayName("{메뉴 ID, 해당 가게}에 해당하는 메뉴 지우기")
    public void deleteMenuList_WhenDeleteMenuListByStoreId_ThenDeleteMenu() throws Exception {
        //given
        doNothing().when(menuService).deleteMenuByStoreIdAndMenuId(anyLong(), anyList());
       //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL,1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(
                        objectMapper
                                .writeValueAsString(new MenuIdList(ID_LIST)))
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("menu/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 ID")
                        ),
                        requestFields(
                                fieldWithPath("menuIdList[]").type(JsonFieldType.ARRAY).description("삭제할 메뉴 ID 리스트")
                        )
                ));

    }
    @Test
    @DisplayName("메뉴 여러개에 대한 업데이트 요청 시 업데이트 수행")
    public void updateMenuList_WhenUpdateMenuList_ThenReturnUpdatedMenu() throws Exception {
        //given
        when(menuService.updateList(anyLong(), any())).thenReturn(MENU_RESPONSE_LIST);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.patch(URL,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .writeValueAsString(MENU_UPDATE_LIST_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("menu/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 ID")
                        ),
                        requestFields(
                                fieldWithPath("menuUpdateList[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("menuUpdateList[].price").type(JsonFieldType.NUMBER).description("메뉴의 가격"),
                                fieldWithPath("menuUpdateList[].menuId").type(JsonFieldType.NUMBER).description("메뉴의 고유 id")

                        ),
                        responseFields(
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("메뉴의 가격"),
                                fieldWithPath("data[].menuId").type(JsonFieldType.NUMBER).description("메뉴의 고유 id")

                        )
                ));

    }

}
