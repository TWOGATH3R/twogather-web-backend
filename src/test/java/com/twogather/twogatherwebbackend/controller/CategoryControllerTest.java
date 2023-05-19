package com.twogather.twogatherwebbackend.controller;

import com.twogather.twogatherwebbackend.service.CategoryService;
import com.twogather.twogatherwebbackend.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.twogather.twogatherwebbackend.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest extends ControllerTest{
    @MockBean
    private CategoryService categoryService;
    @MockBean
    private StoreService storeService;

    @Test
    @DisplayName("카테고리 정보 얻어오기")
    public void getCategoryInfos_WhenGetCategoryThenAllCategoryInfos() throws Exception {
        //given
        when(categoryService.getCategoryInfos()).thenReturn(CATEGORY_RESPONSE_LIST);
        //when
        //then

        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("category/get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("data[].categoryId").type(JsonFieldType.NUMBER).description("카테고리 고유 ID"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("카테고리이름")

                        )
                ));

    }
    @Test
    @DisplayName("가게 카테고리 update")
    public void setCategoriesForStore_WhenSetCategoryThenCategoryInfo() throws Exception {
        //given
        when(categoryService.setCategoriesForStore(anyLong(), anyLong())).thenReturn(CATEGORY_RESPONSE);
        //when
        //then

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/stores/{storeId}/categories/{categoryId}",1,2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("category/patch",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("가게 고유 id"),
                                parameterWithName("categoryId").description("가게 카테고리의 고유 id")
                        ),
                        responseFields(
                                fieldWithPath("data.categoryId").type(JsonFieldType.NUMBER).description("카테고리 고유 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("카테고리이름")

                        )
                ));

    }
}
