package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.service.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {
    @Autowired
    private EmailService emailService;

    /* 자꾸 이메일이 와서 주석처리
    @Test
    public void sendEmail(){
        String email = "firefly_0@naver.com";
        String token = emailService.sendEmail(email).getVerificationCode();
        Assertions.assertNotNull(token);
        System.out.println("token is: " + token);
    }*/
}
