package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.service.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {
    @Autowired
    private EmailService emailService;

    @Test
    public void sendEmail(){
        String email = "firefly_0@naver.com";
        String token = emailService.sendEmail(email).getToken();
        Assertions.assertNotNull(token);
        System.out.println("token is: " + token);
    }
}
