package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.Store;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.StoreRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("test")
public class DataLoader implements CommandLineRunner {
    @Autowired
    private ConsumerRepository consumerRepository;
    @Autowired
    private StoreOwnerRepository storeOwnerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StoreRepository storeRepository;

    public StoreOwner owner;
    public Consumer consumer;
    public Store store;

    @Override
    public void run(String... args) {
        Consumer consumer1 =
                new Consumer("consumer@naber.com", passwordEncoder.encode("asdasd!123"),
                        "홍길동손님", "01012341234", AuthenticationType.CONSUMER, true);
        StoreOwner owner1 = new StoreOwner("owner@naver.com", passwordEncoder.encode("asdasd!123"),
                "김순순사장", "01011111111",
                "김순순", "0000000000", LocalDate.now(), AuthenticationType.OWNER, true);
        consumer = consumerRepository.save(consumer1);
        owner = storeOwnerRepository.save(owner1);
        Store store1 = new Store(owner, null,null, "김가네", "전주시 어쩌고 어쩌고", "063-234-1222", true, "");
        store = storeRepository.save(store1);


    }
}