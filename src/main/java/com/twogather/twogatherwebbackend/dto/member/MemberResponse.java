package com.twogather.twogatherwebbackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long memberId;
    private String username;
    private String email;
    private String name;
}
