package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final ConsumerRepository consumerRepository;
    private final StoreOwnerRepository storeOwnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;

    @Override
    public void run(String... args) {
        Consumer consumer1 = new Consumer(
                "consumer1",
                "consumer@naber.com",
                passwordEncoder.encode("asdasd!123"), "홍길동손님", AuthenticationType.CONSUMER, true);
        StoreOwner owner1 = new StoreOwner(
                "owner1",
                "owner@naver.com",  passwordEncoder.encode("asdasd!123"),
                "김순순사장", AuthenticationType.STORE_OWNER, true);
        consumerRepository.save(consumer1);
        StoreOwner savedOwner = storeOwnerRepository.save(owner1);
        Store store = new Store(savedOwner, null, null, "김김분식집","전주시 어쩌고 어쩌고", "063-231-4222", StoreStatus.APPROVED, null);
        storeRepository.save(store);
    }
}