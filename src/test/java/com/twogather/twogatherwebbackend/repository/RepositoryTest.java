package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.config.QueryDslConfig;
import com.twogather.twogatherwebbackend.domain.Review;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreStatus;
import com.twogather.twogatherwebbackend.repository.review.ReviewRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

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
    @Autowired
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


    protected Store store1;
    protected Store store2;
    protected Store store3;
    protected Store store4;

}