package com.twogather.twogatherwebbackend.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PrivateConstants {
    @Value("${jwt.secret}")
    public String JWT_SECRET = null;

    @Value("${jwt.expiration-time}")
    public int EXPIRATION_TIME = 0;

    @Value("${jwt.token-prefix}")
    public String TOKEN_PREFIX = null;

    @Value("${jwt.header-string}")
    public String HEADER_STRING = null;

    @Value("${api.validate.service.key}")
    public String API_KEY = null;

    @Value("${api.validate.url}")
    public String API_URL = null;
}