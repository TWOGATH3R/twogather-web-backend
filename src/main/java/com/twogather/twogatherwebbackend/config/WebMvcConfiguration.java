package com.twogather.twogatherwebbackend.config;
import com.twogather.twogatherwebbackend.valid.BusinessHourValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final BusinessHourValidator validBusinessHourValidator;

    public WebMvcConfiguration(BusinessHourValidator validBusinessHourValidator) {
        this.validBusinessHourValidator = validBusinessHourValidator;
    }

    @Override
    public Validator getValidator() {
        return validBusinessHourValidator;
    }
}