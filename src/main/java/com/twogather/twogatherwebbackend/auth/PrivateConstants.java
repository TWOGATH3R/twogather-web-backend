package com.twogather.twogatherwebbackend.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PrivateConstants {
    @Value("${jwt.secret}")
    public String JWT_SECRET = null;

    @Value("${jwt.access-token.expiration-time}")
    public int ACCESS_TOKEN_EXPIRATION_TIME = 0;

    @Value("${jwt.refresh-token.expiration-time}")
    public int REFRESH_TOKEN_EXPIRATION_TIME = 0;

    public String TOKEN_PREFIX = "Bearer ";

    @Value("${jwt.access-header}")
    public String ACCESS_TOKEN_HEADER = null;

    @Value("${jwt.refresh-header}")
    public String REFRESH_TOKEN_HEADER = null;

    @Value("${api.validate.service.key}")
    public String API_KEY = null;

    @Value("${api.validate.url}")
    public String API_URL = null;
}