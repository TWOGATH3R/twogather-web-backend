package com.twogather.twogatherwebbackend.service;

import com.google.common.util.concurrent.RateLimiter;
import com.twogather.twogatherwebbackend.dto.email.VerificationCodeResponse;
import com.twogather.twogatherwebbackend.exception.EmailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static com.twogather.twogatherwebbackend.exception.EmailException.EmailErrorCode.EMAIL_SENDING_ERROR;
import static com.twogather.twogatherwebbackend.exception.EmailException.EmailErrorCode.EMAIL_VERIFICATION_REQUEST_IS_AVAILABLE_ONLY_ONCE_EVERY_5_MINUTES;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    // Create a rate limiter that allows 1 request per 5 minutes
    //private static final RateLimiter rateLimiter = RateLimiter.create(0.0033);
    private static final RateLimiter rateLimiter = RateLimiter.create(1);//TODO: 다시 고치기. 테스트에서만 1초에 1개 허용 
    
    public VerificationCodeResponse sendEmail(String to) {
        checkRateLimiter();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[TwoGather] Email Verification");

        String verificationCode = generateToken(6);

        String body = String.format("Your TwoGather email verification code is: %s", verificationCode);
        message.setText(body);

        send(message);

        return new VerificationCodeResponse(verificationCode);
    }
    private void send(SimpleMailMessage message){
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            log.error("Error sending email: {}", e.getMessage());
            throw new EmailException(EMAIL_SENDING_ERROR);
        }
    }
    private void checkRateLimiter() {
        if (!rateLimiter.tryAcquire()) {
            throw new EmailException(EMAIL_VERIFICATION_REQUEST_IS_AVAILABLE_ONLY_ONCE_EVERY_5_MINUTES);
        }
    }
    private static String generateToken(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
