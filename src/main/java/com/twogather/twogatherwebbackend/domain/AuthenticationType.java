package com.twogather.twogatherwebbackend.domain;

public enum AuthenticationType {
    CONSUMER, // 일반 사용자
    OWNER; // 상점주

    public String authority() {
        return "ROLE_" + name();
    }
}
