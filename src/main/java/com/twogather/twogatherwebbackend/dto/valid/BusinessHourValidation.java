package com.twogather.twogatherwebbackend.dto.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BusinessHourValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessHourValidation {
    String message() default "Invalid business hour information";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}