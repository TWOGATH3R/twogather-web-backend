package com.twogather.twogatherwebbackend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class SecurityUtils {
    private SecurityUtils(){}
    // Security Context의 Authentication객체를 이용해 username을 return해주는 간단한 유틸성 메서드
    // JwtFilter.doFilter()에서 request가 들어올때 securityContext에 authentication객체를 저장해서 여기서 사용이 된다
    public static Optional<String> getCurrentUsername(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null){
            log.debug("Security Context에 인증정보가 없습니다.");
            return Optional.empty();
        }
        String username = null;
        if(authentication.getPrincipal() instanceof UserDetails){
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        }else if(authentication.getPrincipal() instanceof String){
            username = (String) authentication.getPrincipal();
        }
        return Optional.ofNullable(username);
    }
}
