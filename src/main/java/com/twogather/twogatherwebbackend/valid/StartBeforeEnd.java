package com.twogather.twogatherwebbackend.valid;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartTimeBeforeEndTimeValidator.class)
@Documented
public @interface StartBeforeEnd {

    String message() default "startTime must be before endTime";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}