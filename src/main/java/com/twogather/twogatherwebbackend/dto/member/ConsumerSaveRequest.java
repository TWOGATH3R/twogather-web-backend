package com.twogather.twogatherwebbackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConsumerSaveRequest extends MemberSave {
    public ConsumerSaveRequest(String email, String password, String name, String phone) {
        super(email, password, name, phone);
    }
}
