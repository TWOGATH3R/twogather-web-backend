package com.twogather.twogatherwebbackend;

import com.twogather.twogatherwebbackend.dto.member.StoreOwnerSaveUpdateRequest;
import com.twogather.twogatherwebbackend.dto.valid.BizRegNumberValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Primary
public class BizRegNumberValidatorStub extends BizRegNumberValidator {

    @Override
    public boolean isValid(StoreOwnerSaveUpdateRequest value, ConstraintValidatorContext context) {
        return true;
    }
}
