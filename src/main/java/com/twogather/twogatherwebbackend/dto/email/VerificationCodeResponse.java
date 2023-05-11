package com.twogather.twogatherwebbackend.dto.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class VerificationCodeResponse {
    private String verificationCode;
}
