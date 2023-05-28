package com.twogather.twogatherwebbackend.acceptance;

import com.twogather.twogatherwebbackend.domain.*;
import com.twogather.twogatherwebbackend.repository.StoreOwnerRepository;
import com.twogather.twogatherwebbackend.repository.store.StoreRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class TestHelper {
    public static StoreOwner createOwner(StoreOwnerRepository ownerRepository, PasswordEncoder passwordEncoder) {
        String email = "email@naver.cim";
        String password = passwordEncoder.encode("asd12345");
        String name = "name";
        String phoneNumber = "000000000";
        String username = "username1";
        String businessName = "김머시기";
        LocalDate businessStartDate = java.time.LocalDate.now();
        AuthenticationType authenticationType = AuthenticationType.STORE_OWNER;
        boolean active = true;
        return ownerRepository.save(
                new StoreOwner(
                username,
                email, password, name, phoneNumber,
                businessName,businessStartDate,
                authenticationType, active
        ));
    }

    public static Store createStore(StoreRepository storeRepository, StoreOwner owner) {
        return storeRepository.save(new Store(owner, null, null, "storeName", "storeAddress", "063-231-400", StoreApprovalStatus.APPROVED, ""));
    }
    public static void createAuthority(StoreOwner owner){
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_STORE_OWNER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(owner.getUsername(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    public static void createAuthority(Consumer consumer){
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_CONSUMER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(consumer.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
