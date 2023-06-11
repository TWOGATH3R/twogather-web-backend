package com.twogather.twogatherwebbackend.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.twogather.twogatherwebbackend.dto.Response;
import com.twogather.twogatherwebbackend.dto.comment.CommentResponse;
import com.twogather.twogatherwebbackend.dto.comment.CommentSaveUpdateRequest;
import com.twogather.twogatherwebbackend.repository.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static com.twogather.twogatherwebbackend.util.TestUtil.convert;


public class CommentAcceptanceTest extends AcceptanceTest{
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    public void init(){
        super.setUp();
        registerOwner();
        registerStore();
        approveStore();
        registerConsumer();
    }

    @Test
    @DisplayName("consumer가 comment 등록 후 확인")
    public void whenRegisterComment_ThenSuccess(){
        //given
        Long reviewId = registerReview();
        String content = "엥 뭐가맛있다는 거죠? 맛이 완전 없어요";
        String url = "/api/stores/"+ storeId + "/reviews/" + reviewId + "/comments";

        //when
        CommentResponse response = convert(doPost(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken(),
                new CommentSaveUpdateRequest(content))
                .extract().as(Response.class), new TypeReference<CommentResponse>() {});

        //then
        Assertions.assertEquals(response.getCreateDate(), LocalDate.now());
        Assertions.assertEquals(response.getIsOwner(), false);
        Assertions.assertEquals(commentRepository.findById(response.getCommentId()).get().getContent(),content );
    }


    @Test
    @DisplayName("consumer가 comment 업데이트 후 확인")
    public void whenUpdateComment_ThenSuccess(){
        //given
        Long reviewId = registerReview();
        Long commentId = registerComment(reviewId);
        String url = "/api/stores/"+ storeId + "/reviews/" + reviewId + "/comments/" + commentId;
        String updateContent = "앗 말실수 지워버려야지";
        //when
        doPut(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken(),
                new CommentSaveUpdateRequest(updateContent));

        //then
        Assertions.assertEquals(commentRepository.findById(commentId).get().getContent(), updateContent);
    }

    @Test
    @DisplayName("가게주인 comment 등록 후 확인")
    public void whenRegisterCommentByOwner_ThenSuccess(){
        //given
        Long reviewId = registerReview();
        String content = "맛없어서 죄송합니다";
        String url = "/api/stores/"+ storeId + "/reviews/" + reviewId + "/comments";

        //when
        CommentResponse response = convert(doPost(url,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken(),
                new CommentSaveUpdateRequest(content))
                .extract().as(Response.class), new TypeReference<CommentResponse>() {});

        //then
        Assertions.assertEquals(response.getIsOwner(), true);
        Assertions.assertEquals(response.getCreateDate(), LocalDate.now());
        Assertions.assertEquals(commentRepository.findById(response.getCommentId()).get().getContent(),content);
    }

    @Test
    @DisplayName("다른사람이 작성한 comment 삭제 불가")
    public void whenDeleteCommentByOtherPeople_ThenThrowException(){
        //given
        Long reviewId = registerReview();
        Long commentId = registerComment(reviewId);
        String url = "/api/stores/"+ storeId + "/reviews/" + reviewId + "/comments/" + commentId;
        //when
        doDelete(url,
                ownerToken.getRefreshToken(),
                ownerToken.getAccessToken())
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all();

        //then
        Assertions.assertTrue(commentRepository.findById(commentId).isPresent());
    }
    @Test
    @DisplayName("당사자가 comment 삭제하면 성공")
    public void whenDeleteCommentByMine_ThenSuccess(){
        //given
        Long reviewId = registerReview();
        Long commentId = registerComment(reviewId);
        String url = "/api/stores/"+ storeId + "/reviews/" + reviewId + "/comments/" + commentId;
        //when
        doDelete(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken())
                .statusCode(HttpStatus.OK.value());

        //then
        Assertions.assertFalse(commentRepository.findById(commentId).isPresent());
    }
    private Long registerComment(Long reviewId){
        String content = "엥 뭐가맛있다는 거죠? 맛이 완전 없어요";
        String url = "/api/stores/"+ storeId + "/reviews/" + reviewId + "/comments";

        CommentResponse response = convert(doPost(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken(),
                new CommentSaveUpdateRequest(content))
                .extract().as(Response.class), new TypeReference<CommentResponse>() {});

        return response.getCommentId();
    }
}
