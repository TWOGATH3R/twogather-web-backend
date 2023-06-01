package com.twogather.twogatherwebbackend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtils {
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null){
            log.debug("Security Context 에 인증정보가 없습니다.");
            return null;
        }
        return authentication.getName();
    }
}
