package com.twogather.twogatherwebbackend.config;

import com.twogather.twogatherwebbackend.util.CacheNames;
import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.session.host}")
    private String hostName;

    @Value("${spring.redis.session.port}")
    private int port;


    @Bean
    public RedisCacheManager cacheManager(
            // 위에서 정의한 레디스 연결 팩토리를 주입
            RedisConnectionFactory connectionFactory) {

        // 레디스 캐시 설정을 정의. 기본적으로 객체는 GenericJackson2JsonRedisSerializer 를 사용하여 직렬화됩니다.
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 캐시 이름에 따른 캐시 구성을 저장하는 맵을 만듭니다.
        Map<String, RedisCacheConfiguration> redisCacheConfigMap = new HashMap<>();
        // "TOP_STORE"라는 캐시에 대한 만료 시간을 1시간으로 설정
        redisCacheConfigMap.put(CacheNames.TOP_STORE, defaultConfig.entryTtl(Duration.ofHours(1)));
        // "xxx"라는 캐시에 대한 만료 시간을 5초로 설정
        //redisCacheConfigMap.put(CacheNames.xxx, defaultConfig.entryTtl(Duration.ofSeconds(5L)));

        // 주어진 구성을 사용하여 레디스 캐시 매니저를 만듭니다.
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(redisCacheConfigMap)
                .build();

        return redisCacheManager;
    }
}