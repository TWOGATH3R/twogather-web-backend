package com.twogather.twogatherwebbackend.acceptance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyToken {
    private String accessToken;
    private String refreshToken;
}
