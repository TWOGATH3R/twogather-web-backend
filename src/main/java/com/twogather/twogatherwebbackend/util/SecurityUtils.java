package com.twogather.twogatherwebbackend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class SecurityUtils {
    //Security Context 에 저장되어있는 인증 객체(유저 객체) 가져오기
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null){
            log.debug("Security Context 에 인증정보가 없습니다.");
            return null;
        }
        User user = (User) authentication.getPrincipal();

        return user;
    }
}
