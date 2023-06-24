package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.config.QueryDslConfig;
import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@DataJpaTest
@Import(QueryDslConfig.class)
@Transactional
public class RepositoryTest {
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected LikeRepository likeRepository;
    @Autowired
    protected StoreRepository storeRepository;
    @Autowired
    protected ConsumerRepository consumerRepository;
    @Autowired
    protected StoreOwnerRepository ownerRepository;
    @PersistenceContext
    protected EntityManager em;
    protected PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    protected ReviewRepository reviewRepository;
    @Autowired
    protected KeywordRepository keywordRepository;
    @Autowired
    protected ImageRepository imageRepository;
    @Autowired
    protected StoreKeywordRepository storeKeywordRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected BusinessHourRepository businessHourRepository;

    protected Store store;
    protected Consumer consumer;

    public void createStore(){
        store = storeRepository.save(Store.builder().address("서울시 어쩌고 어쩌고").name("가게1").build());
   }
   public void createConsumer(){
       consumer = consumerRepository.save(new Consumer("username", "email@naver.com", "asdasd123", "김뿡치", AuthenticationType.CONSUMER, true));
   }

}