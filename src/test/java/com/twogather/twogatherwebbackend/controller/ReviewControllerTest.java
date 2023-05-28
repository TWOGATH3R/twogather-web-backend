package com.twogather.twogatherwebbackend.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDate;
import java.util.List;

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
public class ReviewControllerTest extends ControllerTest {
    @MockBean
    private ReviewService reviewService;
    private static final String URL = "/api/stores/{storeId}/reviews";


//    @Test
//    @DisplayName("페이징된 리뷰 목록 오름차순 정상 조회")
//    public void WhenGetReviewsByPageWithAsc_ThenResponseReviewsAsc() throws Exception {
//        //TODO: 테스트코드 작성
//    }
//
//    @Test
//    @DisplayName("페이지 번호로 음수 또는 정수가 아닌 값이 들어오면 실패(400)")
//    public void WhenPageIsNegative_ThenResponseStatus400() throws Exception {
//        //TODO: 테스트코드 작성
//    }
//
//    @Test
//    @DisplayName("페이징 크기로 음수 또는 정수가 아닌 값이 들어오면 실패(400)")
//    public void WhenPageSizeIsNegative_ThenResponseStatus400() throws Exception {
//        //TODO: 테스트코드 작성
//    }
//
//    @Test
//    @DisplayName("페이징 크기보다 적은 리뷰 수가 존재한다면, 존재하는 리뷰들만 정상적으로 조회")
//    public void WhenGivenPageSizeBiggerThanActualReviews_ThenResponseReviewsWell() throws Exception {
//        //TODO: 테스트코드 작성
//    }
//
//    @Test
//    @DisplayName("존재하는 페이지 수보다 큰 페이지 번호를 요청한다면 실패(404)")
//    public void WhenGivenPageLargerThanActualPages_ThenResponseStatus404() throws Exception {
//        //TODO: 테스트코드 작성
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 column을 기준으로 정렬하려고 시도할 시 실패(400)")
//    public void WhenInvalidColumn_ThenResponseStatus400() throws Exception {
//        //TODO: 테스트코드 작성
//    }

    @Test
    @DisplayName("페이징된 리뷰 목록 조회")
    public void WhenGetReviewsByStoreId_ThenResponseReviews() throws Exception {
        Page<StoreDetailReviewResponse> MY_REVIEW_LIST = new PageImpl<>(List.of(
                new StoreDetailReviewResponse(1L, 1L, "맛잇서요", 5.0, LocalDate.of(2022, 1, 5), "김뿡치", 5.0),
                new StoreDetailReviewResponse(1L, 2L, "맛잇서요", 5.0, LocalDate.of(2022, 1, 5), "김뿡치", 5.0),
                new StoreDetailReviewResponse(1L, 3L, "맛잇서요", 5.0, LocalDate.of(2022, 1, 5), "김뿡치", 5.0)
        ));

        when(reviewService.getReviewsByStoreId(anyLong(), any(), any(), anyInt(), anyInt()))
                .thenReturn(MY_REVIEW_LIST);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/stores/{storeId}/reviews", 1)
                        .param("orderBy", "desc")
                        .param("orderColumn", "createdDate")
                        .param("page", "0")
                        .param("size", "10")
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
                                        parameterWithName("orderBy").description("정렬 방향 (asc 또는 desc). 기본값은 desc입니다."),
                                        parameterWithName("orderColumn").description("정렬 대상 컬럼. 기본값은 createdDate입니다."),
                                        parameterWithName("page").description("조회할 페이지 번호. 기본값은 0입니다."),
                                        parameterWithName("size").description("한 페이지에 조회할 리뷰 개수. 기본값은 10입니다.")
                                ),
                                responseFields(
                                        fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("조회한 리뷰 목록"),
                                        fieldWithPath("data.content[].consumerName").type(JsonFieldType.STRING).description("리뷰를 작성한 사용자의 이름"),
                                        fieldWithPath("data.content[].consumerId").type(JsonFieldType.NUMBER).description("리뷰를 작성한 사용자의 고유 ID"),
                                        fieldWithPath("data.content[].consumerAvgScore").type(JsonFieldType.NUMBER).description("리뷰를 작성한 사용자의 평균 평점"),
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
                        )
                );
    }

    @Test
    @DisplayName("내가 작성한 리뷰 목록")
    public void getMyReviewInfos_WhenGetMyReviews_ThenReturnReviewInfos() throws Exception {
        //given
        when(reviewService.getMyReviewInfos(anyLong(), any(), any(), anyInt(), anyInt())).thenReturn(MY_REVIEW_LIST);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get(URL + "/members/{memberId}", 1, 2)
                        .param("orderBy", "desc")
                        .param("orderColumn", "createdDate")
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
                                parameterWithName("memberId").description("리뷰를 조회할 사용자의 고유 ID"),
                                parameterWithName("storeId").description("리뷰다는 가게의 고유 ID")
                        ),
                        requestParameters(
                                parameterWithName("orderBy").description("정렬 방향 (asc 또는 desc). 기본값은 desc입니다."),
                                parameterWithName("orderColumn").description("정렬 대상 컬럼. 기본값은 createdDate입니다."),
                                parameterWithName("page").description("조회할 페이지 번호. 기본값은 0입니다."),
                                parameterWithName("size").description("한 페이지에 조회할 리뷰 개수. 기본값은 5입니다.")
                        ),
                        responseFields(
                                fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("조회한 리뷰 목록"),
                                fieldWithPath("data.content[].url").type(JsonFieldType.STRING).description("리뷰단 가게의 대표사진"),
                                fieldWithPath("data.content[].storeName").type(JsonFieldType.STRING).description("가게 이름"),
                                fieldWithPath("data.content[].storeAddress").type(JsonFieldType.STRING).description("가게 주소"),
                                fieldWithPath("data.content[].consumerName").type(JsonFieldType.STRING).description("리뷰 작성자의 이름"),
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
    public void upload_WhenUploadReview_ThenReviewInfo() throws Exception {
        //given
        when(reviewService.save(any())).thenReturn(REVIEW_RESPONSE);
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.post(URL, 1)
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
                                parameterWithName("storeId").description("리뷰다는 가게의 고유 ID")
                        ),
                        requestFields(
                                fieldWithPath("consumerId").type(JsonFieldType.NUMBER).description("리뷰작성자의 고유 id"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER).description("가게의 고유 id"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat())
                        ),
                        responseFields(
                                fieldWithPath("data.consumerId").type(JsonFieldType.NUMBER).description("리뷰작성자의 고유 id"),
                                fieldWithPath("data.consumerName").type(JsonFieldType.STRING).description("리뷰작성자의 이름"),
                                fieldWithPath("data.consumerAvgScore").type(JsonFieldType.NUMBER).description("리뷰를 작성한 사용자의 평균 평점"),
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
        mockMvc.perform(RestDocumentationRequestBuilders.put(URL + "/{writerId}", 1, 2)
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
                                parameterWithName("writerId").description("리뷰 작성한사람의 고유 id"),
                                parameterWithName("storeId").description("리뷰다는 가게의 고유 ID")
                        ),
                        requestFields(
                                fieldWithPath("consumerId").type(JsonFieldType.NUMBER).description("리뷰 작성자의 고유 id"),
                                fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("리뷰 고유 id"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰작성내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("리뷰점수").attributes(getScoreFormat())
                        ),
                        responseFields(
                                fieldWithPath("data.consumerId").type(JsonFieldType.NUMBER).description("리뷰 작성자의 고유 id"),
                                fieldWithPath("data.consumerName").type(JsonFieldType.STRING).description("리뷰 작성자의 이름"),
                                fieldWithPath("data.consumerAvgScore").type(JsonFieldType.NUMBER).description("리뷰 자자의 평균 평점"),
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
        //given
        doNothing().when(reviewService).delete(anyLong());
        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete(URL + "/{reviewId}", 1, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(document("review/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("리뷰 단 가게의 고유 id"),
                                parameterWithName("reviewId").description("리뷰의 고유 id")
                        )
                ));

    }


}
