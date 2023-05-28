package com.twogather.twogatherwebbackend.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class ConsumerResponse extends MemberResponse {
    public ConsumerResponse(Long memberId, String username, String name, String email){
        super(memberId,username,email,name);
    }
}
