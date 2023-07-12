package com.twogather.twogatherwebbackend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RefreshTokenValue {
    private Long memberId;
    private String role;
    private String username;
    private String name;
    private String password;
}