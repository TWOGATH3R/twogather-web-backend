package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.service.KeywordService;
import com.twogather.twogatherwebbackend.service.LikeService;
import com.twogather.twogatherwebbackend.service.StoreKeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.twogather.twogatherwebbackend.TestConstants.EMAIL_REQUEST;
import static com.twogather.twogatherwebbackend.TestConstants.VERIFICATION_CODE_RESPONSE;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(LikeController.class)
public class LikeControllerTest extends ControllerTest {
    @MockBean
    private LikeService likeService;

    @Test
    public void addStoreLike() throws Exception {
        doNothing().when(likeService).addStoreLike(anyLong());
        //when
        //then
        mockMvc.perform(post("/api/stores/{storeId}/likes", 1))
                .andExpect(status().isOk())
                .andDo(document("like/setLike",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("좋아요를 누를 대상 가게 id")
                        )
                ));
    }
    @Test
    public void deleteStoreLike() throws Exception {
        doNothing().when(likeService).addStoreLike(anyLong());
        //when
        //then
        mockMvc.perform(delete("/api/stores/{storeId}/likes", 1))
                .andExpect(status().isOk())
                .andDo(document("like/deleteLike",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("좋아요를 누를 대상 가게 id")
                        )
                ));
    }

}
