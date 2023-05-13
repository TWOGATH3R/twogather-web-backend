package com.twogather.twogatherwebbackend.config;

import com.twogather.twogatherwebbackend.BizRegNumberValidatorStub;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Primary
    @Bean
    public BizRegNumberValidatorStub bizRegNumberValidator() {
        return new BizRegNumberValidatorStub();
    }

}