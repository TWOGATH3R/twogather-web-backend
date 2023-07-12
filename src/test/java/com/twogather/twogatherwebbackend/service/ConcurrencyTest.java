package com.twogather.twogatherwebbackend.service;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.dto.store.StoreSaveUpdateResponse;
import com.twogather.twogatherwebbackend.exception.StoreException;
import com.twogather.twogatherwebbackend.repository.*;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import com.twogather.twogatherwebbackend.valid.BizRegNumberValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static com.twogather.twogatherwebbackend.util.TestConstants.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ConcurrencyTest {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    StoreKeywordRepository storeKeywordRepository;
    @Autowired
    StoreService storeService;
    /*
    @BeforeEach
    void init(){
        Category category = categoryRepository.save(new Category("양식"));
        Keyword keyword1 = keywordRepository.save(new Keyword("맛있는"));
        Keyword keyword2 = keywordRepository.save(new Keyword("KEYWORD2"));
        Keyword keyword3 = keywordRepository.save(new Keyword("KEYWORD3"));
        Store store = storeRepository.save(Store.builder().name(STORE_SAVE_REQUEST.getStoreName()).category(category).build());
        storeKeywordRepository.save(new StoreKeyword(store, keyword1));
        storeKeywordRepository.save(new StoreKeyword(store, keyword2));
        storeKeywordRepository.save(new StoreKeyword(store, keyword3));
   }
    @Test
    public void testUpdateConcurrency() throws InterruptedException {
        int threads = 100;  // 실행하려는 스레드의 수를 정의합니다.
        ExecutorService service = Executors.newFixedThreadPool(threads);  // 이 서비스는 각 작업을 수행하는데 있어서, 가능한 여러 풀링된 스레드 중 하나를 사용합니다.

        CountDownLatch latch = new CountDownLatch(threads);  // 다른 스레드에서 수행되는 일련의 작업이 완료될 때까지 하나 이상의 스레드가 대기하도록 하는 동기화 보조 도구입니다. 여기서 래치의 카운트는 스레드의 수로 설정됩니다.

        for (int i = 0; i < threads; i++) {
            service.submit(() -> {  // 이 람다 함수는 ExecutorService의 새로운 스레드에서 실행됩니다.
                try{
                    storeService.update(STORE_ID, STORE_UPDATE_REQUEST);  // 실제 매개 변수로 바꾸어야 합니다. 이 함수는 우리가 테스트하고자 하는 함수입니다.
                }finally {
                    latch.countDown();
                }

            });
        }
        latch.await();  // 래치가 0으로 카운트다운 될 때까지 기다립니다. 이렇게 하면 모든 스레드가 동시에 시작됩니다.

        Store store = storeRepository.findById(STORE_ID).get();
        Assertions.assertEquals(store.getName(), STORE_UPDATE_REQUEST.getStoreName());
    }

    @Test
    public void testUpdateConcurrency2() throws Exception {
        // 서로 다른 두 개의 스레드를 생성합니다.
        Thread threadA = new Thread(() -> {
            // Thread A에서 update() 메소드를 호출합니다.
            storeService.update(STORE_ID, STORE_UPDATE_REQUEST);
        });

        Thread threadB = new Thread(() -> {
            // Thread B에서 update() 메소드를 호출합니다.
            storeService.update(STORE_ID, STORE_UPDATE_REQUEST);
        });

        // 두 스레드를 거의 동시에 시작합니다.
        threadA.start();
        threadB.start();

        // 두 스레드가 모두 종료될 때까지 대기합니다.
        threadA.join();
        assertThrows(StoreException.class, () -> threadB.join());

        // 상점의 최종 상태를 검사합니다.
        Store store = storeRepository.findById(STORE_ID).get();
        Assertions.assertEquals(store.getName(), STORE_UPDATE_REQUEST.getStoreName());

    }*/
}