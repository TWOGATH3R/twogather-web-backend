package com.twogather.twogatherwebbackend.dto.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Constraint(validatedBy = BizRegNumberValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BizRegNumberValidation {
    String message() default "Invalid business registration number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}