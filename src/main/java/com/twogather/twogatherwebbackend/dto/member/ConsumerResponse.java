package com.twogather.twogatherwebbackend.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class ConsumerResponse extends MemberResponse {
    public ConsumerResponse(Long id, String name, String email, String phone){
        super(id, name,email,phone);
    }
}