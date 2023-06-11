package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.dto.keyword.KeywordResponse;
import com.twogather.twogatherwebbackend.service.KeywordService;
import com.twogather.twogatherwebbackend.service.StoreKeywordService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.ArrayList;
import java.util.List;

import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getKeywordFormat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(KeywordController.class)
public class KeywordControllerTest extends ControllerTest{
    @MockBean
    private KeywordService keywordService;
    @MockBean
    private StoreKeywordService storeKeywordService;

    @Test
    public void setStoreKeywordAssociation() throws Exception {
        //given
        long storeId = 1;
        List<String> keywordList = new ArrayList<>(){{
            add("맛있는");
            add("친구랑 가기좋은");
            add( "가족과 함께하는");
        }};
        doNothing().when(storeKeywordService).setStoreKeyword(1l, keywordList);
        //when
        //then
        mockMvc.perform(put("/api/keywords/stores/{storeId}", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(keywordList))
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("keywords/set-keyword-store",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                              parameterWithName("storeId").description("해당 키워드랑 연결할 가게의 고유 ID")
                        ),
                        requestFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("조회할 키워드 목록")
                        )
                ));


    }

    @Test
    public void getKeywordList() throws Exception {
        //given
        Integer count = 3;
        List<KeywordResponse> keywordList = new ArrayList<>(){{
            add(new KeywordResponse(1l, "맛있는"));
            add(new KeywordResponse(2l, "친구랑 가기좋은"));
            add(new KeywordResponse(3l, "가족과 함께하는"));
        }};
        when(keywordService.getNKeywordList(count)).thenReturn(keywordList);
        //when
        //then
        mockMvc.perform(get("/api/keywords")
                        .param("count", count.toString())
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("keywords/get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("count").description("조회할 키워드 개수 ")

                        ),
                        responseFields(
                                fieldWithPath("data[].keywordId").type(JsonFieldType.NUMBER).description("키워드 고유 id"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("키워드명").attributes(getKeywordFormat())
                        )
                ));


    }

}
