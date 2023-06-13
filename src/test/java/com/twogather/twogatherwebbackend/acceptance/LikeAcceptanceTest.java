package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.repository.LikeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class LikeAcceptanceTest extends AcceptanceTest{
    @Autowired
    private LikeRepository likeRepository;

    @BeforeEach
    public void initSetting(){
        registerOwner();
        registerStore();
        approveStore();
        registerConsumer();
        url = "/api/stores/" + storeId + "/likes";
    }
    private String url;

    @Test
    @DisplayName("가게 좋아요를 눌렸을때 디비에 제대로된 관계를 맺으며 저장되었는지 확인")
    public void whenAddStoreLike_ThenSuccessAssociationMemberAndStoreAndLikes() {
        //given
        //when
        //then
        doPost(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken(),null).statusCode(HttpStatus.OK.value());

        Assertions.assertTrue(likeRepository.findByStoreIdAndMemberId(storeId, consumerId).isPresent());

    }
    @Test
    @DisplayName("가게 좋아요를 취소했을 시 가게와 멤버에 대한 좋아요 entity는 삭제 되어있어야한다")
    public void whenDeleteStoreLike_ThenNoLikesEntityWithStoreIdMemberId()  {
        //given
        doPost(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken(),null).statusCode(HttpStatus.OK.value());

        //when
        //then
        doDelete(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken());

        Assertions.assertFalse(likeRepository.findByStoreIdAndMemberId(storeId, consumerId).isPresent());

    }

    @Test
    @DisplayName("가게 좋아요 추가하는 요청을 두번 보냈을땐 두번 저장되지 않고 throw exception")
    public void whenDuplicateAddStoreLike_ThenThrowException() {
        //given
        doPost(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken(),null).statusCode(HttpStatus.OK.value());
        //when,then
        doPost(url,
                consumerToken.getRefreshToken(),
                consumerToken.getAccessToken(),null).statusCode(HttpStatus.BAD_REQUEST.value());

        Assertions.assertTrue(likeRepository.findByStoreIdAndMemberId(storeId, consumerId).isPresent());


    }
    @Test
    @DisplayName("인증되지 않은자가 가게 좋아요를 누르는 경우 throw exception")
    public void whenSetStoreLikeWithAnonymousUser_ThenThrowException() {
        //given
        doPost(url,null,null,null).statusCode(HttpStatus.UNAUTHORIZED.value());
    }


}
