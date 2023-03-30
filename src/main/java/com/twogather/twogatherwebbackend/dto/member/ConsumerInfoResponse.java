package com.twogather.twogatherwebbackend.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class ConsumerInfoResponse extends MemberInfo{
    public ConsumerInfoResponse(String name, String email, String phone){
        super(name,email,phone);
    }
}
