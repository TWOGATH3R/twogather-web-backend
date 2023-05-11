package com.twogather.twogatherwebbackend.domain;

public enum AuthenticationType {
    CONSUMER, // 일반 사용자
    STORE_OWNER, // 상점주
    ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }
}
