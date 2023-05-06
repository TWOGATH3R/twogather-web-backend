package com.twogather.twogatherwebbackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConsumerSaveUpdateRequest extends MemberSaveUpdateRequest {
    public ConsumerSaveUpdateRequest(String email, String password, String name) {
        super(email, password, name);
    }
}
