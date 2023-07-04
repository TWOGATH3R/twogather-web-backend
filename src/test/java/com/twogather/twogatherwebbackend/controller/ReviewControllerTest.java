package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twogather.twogatherwebbackend.dto.comment.CommentResponse;
import com.twogather.twogatherwebbackend.dto.review.ReviewSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.review.StoreDetailReviewResponse;
import com.twogather.twogatherwebbackend.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.List;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
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
public class ReviewControllerTest extends ControllerTest {
    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("지정된 범위(0.5~5.0)를 벗어난 점수 입력 시 요청 거부")
    public void WhenScoreIsInvalid_ThenDenyRequest() throws Exception{
        // given
        ReviewSaveUpdateRequest request = new ReviewSaveUpdateRequest(
                "너무 맛있어서 1000점 드립니다", 1000.0
        );
        String jsonRequest = objectMapper.writeValueAsString(request);

        // when-then
        mockMvc.perform(post("/api/stores/{storeId}/reviews", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("페이징된 리뷰 목록 조회")
    public void WhenGetReviewsByStoreId_ThenResponseReviews() throws Exception {
        //given
        Page<StoreDetailReviewResponse> MY_REVIEW_LIST = new PageImpl<>(List.of(
                new StoreDetailReviewResponse(1L, "맛잇서요", 5.0, LocalDateTime.now(), 1L, "김뿡치", 5.0, new CommentResponse(1L, "감사합니당", LocalDateTime.now())),
                new StoreDetailReviewResponse(1L, "맛잇서요", 5.0, LocalDateTime.now(), 2L, "김뿡치", 5.0, null),
                new StoreDetailReviewResponse(1L, "맛잇서요", 5.0, LocalDateTime.now(), 3L, "김뿡치", 5.0, null)
        ));

        //when
        when(reviewService.getReviewsByStoreId(anyLong(), any()))
                .thenReturn(MY_REVIEW_LIST);

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/stores/{storeId}/reviews", 1)
                        .param("sort", "createdDate,desc")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(
                        document(
                                "review/getReviewsByStoreId",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("storeId").description("리뷰를 조회할 가게의 고유 ID")
                                ),
                                requestParameters(
                                        parameterWithName("sort").description("정렬기준항목과 정렬순서(콤마로 구분)").optional(),
                                        parameterWithName("page").description("조회할 페이지 번호. 기본값은 0입니다."),
                                        parameterWithName("size").description("한 페이지에 조회할 리뷰 개수. 기본값은 5입니다.")
                                ),
                                responseFields(
                                        fieldWithPath("data[].consumerName").type(JsonFieldType.STRING).description("리뷰를 작성한 사용자의 이름"),
                                        fieldWithPath("data[].consumerId").type(JsonFieldType.NUMBER).description("리뷰를 작성한 사용자의 고유 ID"),
                                        fieldWithPath("data[].consumerAvgScore").type(JsonFieldType.NUMBER).description("리뷰를 작성한 사용자의 평균 평점"),
                                        fieldWithPath("data[].reviewId").type(JsonFieldType.NUMBER).description("리뷰의 고유 ID"),
                                        fieldWithPath("data[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                        fieldWithPath("data[].score").type(JsonFieldType.NUMBER).description("리뷰 점수").attributes(getScoreFormat()),
                                        fieldWithPath("data[].createdDate").type(JsonFieldType.STRING).description("리뷰 작성일자").attributes(getDateFormat()),
                                        fieldWithPath("data[].comment").type(JsonFieldType.OBJECT).description("대댓글 정보").optional(),
                                        fieldWithPath("data[].comment.commentId").type(JsonFieldType.NUMBER).description("대댓글 고유 ID"),
                                        fieldWithPath("data[].comment.content").type(JsonFieldType.STRING).description("대댓글 내용"),
                                        fieldWithPath("data[].comment.createdDate").type(JsonFieldType.STRING).description("대댓글 작성일자").attributes(getDateFormat()),
                                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 페이지 수"),
                                        fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("현재 페이지의 아이템 수"),
                                        fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 개수"),
                                        fieldWithPath("isLast").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                        fieldWithPath("isFirst").type(JsonFieldType.BOOLEAN).description("첫 페이지인지 여부"),
                                        fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재페이지가 몇번인지")

                                )
                        )
                );
    }

    @Test
    @DisplayName("내가 작성한 리뷰 목록")
    public void getMyReviewInfos_WhenGetMyReviews_ThenReturnReviewInfos() throws Exception {
        //given

        when(reviewService.getMyReviewInfos(anyLong(), any())).thenReturn(MY_REVIEW_LIST);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/members/{memberId}", 2)
                        .param("sort", "createdAt,desc")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("review/getMyReviewInfos",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("리뷰를 조회할 사용자의 고유 ID")
                        ),
                        requestParameters(
                                parameterWithName("sort").description("정렬기준항목과 정렬순서(콤마로 구분)").optional(),
                                parameterWithName("page").description("조회할 페이지 번호. 기본값은 0입니다.").optional(),
                                parameterWithName("size").description("한 페이지에 조회할 리뷰 개수. 기본값은 5입니다.").optional()
                        ),
                        responseFields(
                                fieldWithPath("data[].url").type(JsonFieldType.STRING).description("리뷰단 가게의 대표사진"),
                                fieldWithPath("data[].storeId").type(JsonFieldType.NUMBER).description("가게 고유 ID"),
                                fieldWithPath("data[].storeName").type(JsonFieldType.STRING).description("가게 이름"),
                                fieldWithPath("data[].storeAddress").type(JsonFieldType.STRING).description("가게 주소"),
                                fieldWithPath("data[].consumerName").type(JsonFieldType.STRING).description("리뷰단 사람의 닉네임"),
                                fieldWithPath("data[].reviewId").type(JsonFieldType.NUMBER).description("리뷰의 고유 ID"),
                                fieldWithPath("data[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("data[].score").type(JsonFieldType.NUMBER).description("리뷰 점수").attributes(getScoreFormat()),
                                fieldWithPath("data[].createdDate").type(JsonFieldType.STRING).description("리뷰 작성일자").attributes(getDateFormat()),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 페이지 수"),
                                fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("현재 페이지의 아이템 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("조회한 리뷰의 총 개수"),
                                fieldWithPath("isLast").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                fieldWithPath("isFirst").type(JsonFieldType.BOOLEAN).description("첫 페이지인지 여부"),
                                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("현재페이지가 몇번인지")

                        )
                ));
    }

    @Test
    @DisplayName("리뷰 업로드")
    public void upload_WhenUploadReview_ThenReviewInfo() throws Exception {
        //given
        when(reviewService.save(anyLong(),any())).thenReturn(REVIEW_RESPONSE);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/stores/{storeId}/reviews", 1,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(REVIEW_SAVE_REQUEST))
                )
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("review/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("리뷰다는 가게의 고유 ID"),
                                parameterWithName("storeId").description("가게 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat())
                        ),
                        responseFields(
                                fieldWithPath("data.reviewId").type(JsonFieldType.NUMBER).description("해당 리뷰의 고유 id"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("data.score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat()),
                                fieldWithPath("data.createdDate").type(JsonFieldType.STRING).description("작성날짜").attributes(getDateFormat()),
                                fieldWithPath("data.consumerName").type(JsonFieldType.STRING).description("리뷰작성자의 이름")

                        )
                ));

    }

    @Test
    @DisplayName("리뷰 업데이트")
    public void update() throws Exception {
        //given
        when(reviewService.update(anyLong(),any())).thenReturn(REVIEW_RESPONSE);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/stores/{storeId}/reviews/{reviewId}", 2,1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                        .content(
                                objectMapper
                                        .registerModule(new JavaTimeModule())
                                        .writeValueAsString(REVIEW_UPDATE_REQUEST))
                )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("review/update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 id"),
                                parameterWithName("storeId").description("가게 고유 id")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat())
                        ),
                        responseFields(
                                fieldWithPath("data.reviewId").type(JsonFieldType.NUMBER).description("해당 리뷰의 고유 id"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("data.score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat()),
                                fieldWithPath("data.createdDate").type(JsonFieldType.STRING).description("작성날짜").attributes(getDateFormat()),
                                fieldWithPath("data.consumerName").type(JsonFieldType.STRING).description("리뷰 작성자의 이름")
                        )
                ));

    }

    @Test
    @DisplayName("리뷰 삭제")
    public void delete() throws Exception {
        //given
        doNothing().when(reviewService).delete(anyLong());
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete( "/api/stores/{storeId}/reviews/{reviewId}", 1,2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(document("review/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰의 고유 id"),
                                parameterWithName("storeId").description("가게 고유 id")
                        )
                ));

    }
}
