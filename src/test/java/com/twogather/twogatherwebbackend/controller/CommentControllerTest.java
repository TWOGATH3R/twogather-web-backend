package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentRequest;
import static com.twogather.twogatherwebbackend.docs.ApiDocumentUtils.getDocumentResponse;
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.getDayOfWeekFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CommentController.class)
public class CommentControllerTest extends ControllerTest{
    @MockBean
    private CommentService commentService;
    private static final String URL = "/api/stores/{storeId}/reviews/{reviewId}/comments";

    @Test
    public void save() throws Exception {
        //given
        when(commentService.save(any())).thenReturn(COMMENT_RESPONSE);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL, 1,2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(COMMENT_SAVE_UPDATE_REQUEST))
                )
                .andExpect(status().isCreated())
                .andDo(document("comment/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("대댓글 달 가게의 ID"),
                                parameterWithName("reviewId").description("대댓글 달 댓글의 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("대댓글의 내용")
                                ),
                        responseFields(
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("대댓글의 내용"),
                                fieldWithPath("data.isOwner").type(JsonFieldType.BOOLEAN).description("대댓글을 쓴 사람이 가게 주인인지에 대한 여부"),
                                fieldWithPath("data.createDate").type(JsonFieldType.STRING).description("대댓글 작성날짜").attributes(getDayOfWeekFormat())

                        )
                ));
    }
    @Test
    public void update() throws Exception {
        //given
        when(commentService.update(any())).thenReturn(COMMENT_RESPONSE);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.put(URL+"/{commentId}", 1,2,3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(COMMENT_SAVE_UPDATE_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("comment/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("대댓글 달 가게의 ID"),
                                parameterWithName("reviewId").description("대댓글 달 댓글의 ID"),
                                parameterWithName("commentId").description("수정할 대댓글의 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("대댓글의 내용")
                        ),
                        responseFields(
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("대댓글의 내용"),
                                fieldWithPath("data.isOwner").type(JsonFieldType.BOOLEAN).description("대댓글을 쓴 사람이 가게 주인인지에 대한 여부"),
                                fieldWithPath("data.createDate").type(JsonFieldType.STRING).description("대댓글 작성날짜").attributes(getDayOfWeekFormat())

                        )
                ));
    }
}
