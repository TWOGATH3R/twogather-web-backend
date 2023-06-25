package com.twogather.twogatherwebbackend.service;

import com.google.common.util.concurrent.RateLimiter;
import com.twogather.twogatherwebbackend.domain.Member;
import com.twogather.twogatherwebbackend.dto.email.VerificationCodeResponse;
import com.twogather.twogatherwebbackend.dto.member.FindPasswordRequest;
import com.twogather.twogatherwebbackend.exception.EmailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

import static com.twogather.twogatherwebbackend.exception.EmailException.EmailErrorCode.EMAIL_SENDING_ERROR;
import static com.twogather.twogatherwebbackend.exception.EmailException.EmailErrorCode.EMAIL_VERIFICATION_REQUEST_IS_AVAILABLE_ONLY_ONCE_EVERY_5_MINUTES;
import static com.twogather.twogatherwebbackend.util.PasswordGenerator.generatePassword;
import static com.twogather.twogatherwebbackend.util.PasswordGenerator.getRandomNumberBetween;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private static final RateLimiter rateLimiter = RateLimiter.create(1);//TODO: 다시 고치기. 테스트에서만 1초에 1개 허용

    public VerificationCodeResponse sendCodeEmail(String to) {
        checkRateLimiter();

        String verificationCode = generateToken(6);
        String body = String.format("Your TwoGather email verification code is: %s", verificationCode);

        send(to, "[TwoGather] email verification",body);

        return new VerificationCodeResponse(verificationCode);
    }
    @Transactional
    public void requestEmailChangePassword(FindPasswordRequest request) {
        int len = getRandomNumberBetween(8,20);
        String newPassword = generatePassword(len);
        String body = String.format("Your New Password is: %s", newPassword);

        Member member = memberService.findMember(request.getEmail(), request.getUsername());

        send(member.getEmail(),"[TwoGather] email verification",body);

        member.update(null,null,passwordEncoder.encode(newPassword),null);
    }
    private void send(String to, String subject, String content){
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(content);
        try {
            javaMailSender.send(mail);
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
