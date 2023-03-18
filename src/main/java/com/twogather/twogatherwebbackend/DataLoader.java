package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final ConsumerRepository consumerRepository;
    private final StoreOwnerRepository storeOwnerRepository;

    @Override
    public void run(String... args) {
        Consumer consumer1 = new Consumer("consumer@naber.com", "asd!123", "홍길동손님", "01012341234");
        StoreOwner owner1 = new StoreOwner("owner@naver.com", "asd!123",
                "김순순사장", "01011111111",
                "김순순", "0000000000", LocalDate.now());
        consumerRepository.save(consumer1);
        storeOwnerRepository.save(owner1);
    }
}