package com.twogather.twogatherwebbackend.repository;

import com.twogather.twogatherwebbackend.auth.PrivateConstants;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
@Repository
public class RefreshTokenRepository {


    private PrivateConstants constants;
    private RedisTemplate redisTemplate;

    public RefreshTokenRepository(final RedisTemplate redisTemplate,
                                  final PrivateConstants constants) {
        this.redisTemplate = redisTemplate;
        this.constants = constants;
    }

    public void save(final String refreshToken, final Long memberId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken, memberId.toString());
        redisTemplate.expire(refreshToken, constants.REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.SECONDS);
    }

    public Optional<Long> findId(final String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        return Optional.ofNullable(Long.valueOf(valueOperations.get(refreshToken)));
    }
}