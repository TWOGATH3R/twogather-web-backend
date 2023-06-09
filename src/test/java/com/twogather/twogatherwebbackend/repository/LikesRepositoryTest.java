package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class LikesRepositoryTest extends RepositoryTest{

    @Test
    public void deleteByStoreIdAndMemberId(){
        //given
        StoreOwner storeOwner = ownerRepository.save(new StoreOwner());
        Store store = storeRepository.save(new Store(storeOwner, "sad","주소","010-1234-1234", "0000000000", "홍길동", LocalDate.now()));
        Consumer consumer = consumerRepository.save(new Consumer("user1","asd@naver.com","asda@1221!#","이름", AuthenticationType.CONSUMER, true));

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
