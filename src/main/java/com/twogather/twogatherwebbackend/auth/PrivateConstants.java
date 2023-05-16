package com.twogather.twogatherwebbackend.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public interface PrivateConstants {
    @Value("${jwt.secret}")
    String JWT_SECRET = null;

    @Value("${jwt.expiration-time}")
    int EXPIRATION_TIME = 0;

    @Value("${jwt.token-prefix}")
    String TOKEN_PREFIX = null;

    @Value("${jwt.header-string}")
    String HEADER_STRING = null;

    @Value("${api.validate.service.key}")
    String API_KEY = null;

    @Value("${api.validate.url}")
    String API_URL = null;
}