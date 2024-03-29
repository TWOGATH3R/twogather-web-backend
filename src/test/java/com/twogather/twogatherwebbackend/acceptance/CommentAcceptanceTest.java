package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;


public class CommentAcceptanceTest extends AcceptanceTest{
    @Autowired
    private CommentRepository commentRepository;

    /*
    //TODO: Comment나 review등록하는 부분을 repository가 아니라 하나의 api요청으로 처리해야한다
    //TODO: review부분이 완성이 되지 않았기에 나중에 구현하도록 주석처리하였다
    //TODO: 리뷰의 경우 가게주인이 작성하면 REJECT돼야함 (테스트 추가하기)
    @BeforeEach
    public void init(){
        super.setUp();
        registerOwner();
        registerStore();
        approveStore();
        registerConsumer();
    }

    @Test
    @DisplayName("comment 등록 후 데이터베이스엔 똑같은 내용이 저장되어있어야한다")
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
    @DisplayName("consumer가 comment 업데이트 후에 업데이트한 내용이 디비에 반영이 되어있어야한다")
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
    @DisplayName("가게주인이 comment를 등록하고 가게주인여부에 대한 응답까지 적절하게 나와야한다")
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
    @DisplayName("다른사람이 작성한 comment 삭제 불가해야한다")
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
    @DisplayName("당사자가 comment 삭제하면 성공해야한다")
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
    }*/
}
