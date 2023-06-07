package com.twogather.twogatherwebbackend;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Tokens {
    private String accessToken;
    private String refreshToken;
    public Tokens(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
