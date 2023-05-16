package com.twogather.twogatherwebbackend.controller;


import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class NoopValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
    }
}