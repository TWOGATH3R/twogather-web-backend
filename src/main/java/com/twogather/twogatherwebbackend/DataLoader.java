package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.domain.AuthenticationType;
import com.twogather.twogatherwebbackend.domain.Consumer;
import com.twogather.twogatherwebbackend.domain.StoreOwner;
import com.twogather.twogatherwebbackend.repository.ConsumerRepository;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final ConsumerRepository consumerRepository;
    private final StoreOwnerRepository storeOwnerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Consumer consumer1 = new Consumer("consumer@naber.com", passwordEncoder.encode("asdasd!123"), "홍길동손님", AuthenticationType.CONSUMER, true);
        StoreOwner owner1 = new StoreOwner("owner@naver.com",  passwordEncoder.encode("asdasd!123"),
                "김순순사장",
                "김순순", "0000000000", LocalDate.now(), AuthenticationType.STORE_OWNER, true);
        consumerRepository.save(consumer1);
        storeOwnerRepository.save(owner1);
    }
}