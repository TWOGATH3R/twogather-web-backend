package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@Transactional
public class LikesRepositoryTest {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ConsumerRepository consumerRepository;
    @Autowired
    private StoreOwnerRepository ownerRepository;
    @Autowired
    private EntityManager em;
    @Test
    public void deleteByStoreIdAndMemberId(){
        //given
        StoreOwner storeOwner = ownerRepository.save(new StoreOwner());
        Store store = storeRepository.save(new Store(storeOwner, "sad","주소","010-1234-1234"));
        Consumer consumer = consumerRepository.save(new Consumer("user1","이름","주소","010-123-123", AuthenticationType.CONSUMER, true));

        //when
        likeRepository.save(new Likes(store,consumer));
        em.flush();
        em.clear();
        int deletedRows = likeRepository.deleteByStoreStoreIdAndMemberMemberId(store.getStoreId(), consumer.getMemberId());
        boolean isExist = likeRepository.findByStoreIdAndMemberId(store.getStoreId(), consumer.getMemberId()).isPresent();

        //then
        Assertions.assertEquals(deletedRows,1);
        Assertions.assertFalse(isExist);
    }
}
