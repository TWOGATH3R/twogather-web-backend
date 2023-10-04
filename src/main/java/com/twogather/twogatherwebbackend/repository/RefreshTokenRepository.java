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
    final private PrivateConstants constants;
    final private RedissonClient redissonClient;

    public RefreshTokenRepository(final RedissonClient redissonClient,
                                  final PrivateConstants constants) {
        this.redissonClient = redissonClient;
        this.constants = constants;
    }


    public void save(final String refreshToken, final Long memberId) {
        RBucket<String> bucket = redissonClient.getBucket(refreshToken);
        bucket.set(memberId.toString(), constants.REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.SECONDS);
    }

    public Optional<Long> findId(final String refreshToken) {
        RBucket<String> bucket = redissonClient.getBucket(refreshToken);
        String memberIdString = bucket.get();
        if (memberIdString == null) {
            return Optional.empty();
        }
        return Optional.of(Long.valueOf(memberIdString));
    }
}