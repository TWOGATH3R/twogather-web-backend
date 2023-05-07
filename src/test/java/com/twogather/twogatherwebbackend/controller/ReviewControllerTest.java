package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.service.ReviewService;
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
import static com.twogather.twogatherwebbackend.docs.DocumentFormatGenerator.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ReviewController.class)
public class ReviewControllerTest extends ControllerTest{
    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("내가 작성한 리뷰 목록")
    public void getMyReviewInfos() throws Exception {
        //given
        when(reviewService.getMyReviewInfos(anyLong(), any(),any(),anyInt(), anyInt())).thenReturn(MY_REVIEW_LIST);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/consumers/{consumerId}", 1)
                        .param("orderBy", "desc")
                        .param("orderColumn", "createdAt")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("review/getMyReviewInfos",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("consumerId").description("리뷰를 조회할 소비자의 고유 ID")
                        ),
                        requestParameters(
                                parameterWithName("orderBy").description("정렬 방향 (asc 또는 desc). 기본값은 desc입니다."),
                                parameterWithName("orderColumn").description("정렬 대상 컬럼. 기본값은 createdAt입니다."),
                                parameterWithName("page").description("조회할 페이지 번호. 기본값은 1입니다."),
                                parameterWithName("size").description("한 페이지에 조회할 리뷰 개수. 기본값은 5입니다.")
                        ),
                        responseFields(
                                fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("조회한 리뷰 목록"),
                                fieldWithPath("data.content[].url").type(JsonFieldType.STRING).description("리뷰단 가게의 대표사진"),
                                fieldWithPath("data.content[].storeName").type(JsonFieldType.STRING).description("가게 이름"),
                                fieldWithPath("data.content[].storeAddress").type(JsonFieldType.STRING).description("가게 주소"),
                                fieldWithPath("data.content[].consumerName").type(JsonFieldType.STRING).description("리뷰단 사람의 이름"),
                                fieldWithPath("data.content[].reviewId").type(JsonFieldType.NUMBER).description("리뷰의 고유 ID"),
                                fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("data.content[].score").type(JsonFieldType.NUMBER).description("리뷰 점수").attributes(getScoreFormat()),
                                fieldWithPath("data.content[].createdDate").type(JsonFieldType.STRING).description("리뷰 작성일자").attributes(getDateFormat()),
                                fieldWithPath("data.pageable").type(JsonFieldType.STRING).description("페이지 정보"),
                                fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 되지 않은 상태인지 여부"),
                                fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 가능한 상태인지 여부"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 페이지 수"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 개수"),
                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지인지 여부"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 조회한 페이지 번호"),
                                fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("조회한 페이지의 리뷰 개수"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("한 페이지당 최대 리뷰 개수"),
                                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("리뷰 조회 결과가 비어있는지 여부")
                                )
                        ));
    }
    @Test
    @DisplayName("리뷰 업로드")
    public void upload() throws Exception {
        //given
        when(reviewService.save(any())).thenReturn(REVIEW_RESPONSE);
        //when
        //then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(REVIEW_SAVE_REQUEST))
                )
                .andExpect(status().isCreated())
                .andDo(document("review/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("consumerId").type(JsonFieldType.NUMBER).description("리뷰작성자의 고유 id"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER).description("가게의 고유 id"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat())
                     ),
                        responseFields(
                                fieldWithPath("data.consumerId").type(JsonFieldType.NUMBER).description("리뷰작성자의 고유 id"),
                                fieldWithPath("data.reviewId").type(JsonFieldType.NUMBER).description("해당 리뷰의 고유 id"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("data.score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat()),
                                fieldWithPath("data.createdDate").type(JsonFieldType.STRING).description("작성날짜").attributes(getDateFormat())

                        )
                ));

    }

    @Test
    @DisplayName("리뷰 업데이트")
    public void update() throws Exception {
        //given
        when(reviewService.update(any())).thenReturn(REVIEW_RESPONSE);
        //when
        //then
        mockMvc.perform(put("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(REVIEW_UPDATE_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(document("review/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("consumerId").type(JsonFieldType.NUMBER).description("리뷰작성자의 고유 id"),
                                fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("리뷰 고유 id"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat())
                        ),
                        responseFields(
                                fieldWithPath("data.consumerId").type(JsonFieldType.NUMBER).description("리뷰작성자의 고유 id"),
                                fieldWithPath("data.reviewId").type(JsonFieldType.NUMBER).description("해당 리뷰의 고유 id"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("data.score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat()),
                                fieldWithPath("data.createdDate").type(JsonFieldType.STRING).description("작성날짜").attributes(getDateFormat())

                        )
                ));

    }
    @Test
    @DisplayName("리뷰 삭제")
    public void delete() throws Exception {
        //givenn
        doNothing().when(reviewService).delete(anyLong());
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/reviews/{reviewId}",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(document("review/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("reviewId").description("삭제할 리뷰 id")
                        )
                ));

    }


}
