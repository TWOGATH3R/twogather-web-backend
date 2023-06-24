package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.Likes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LikeRepositoryTest extends RepositoryTest{
    @BeforeEach
    void init(){
        createStore();
        createConsumer();
    }
    @Test
    public void deleteByStoreStoreIdAndMemberMemberId(){
        //given
        //when
        Likes like = createLike();

        likeRepository.deleteByStoreIdAndMemberId(store.getStoreId(), consumer.getMemberId());
        //likeRepository.deleteById(like.getLikesId());

        //then
        Assertions.assertFalse(likeRepository.existsById(like.getLikesId()));
    }
    public Likes createLike(){
        return likeRepository.save(new Likes(store,consumer));
    }
}
