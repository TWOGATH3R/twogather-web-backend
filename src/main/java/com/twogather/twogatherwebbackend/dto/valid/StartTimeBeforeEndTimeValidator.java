package com.twogather.twogatherwebbackend.dto.valid;

import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartTimeBeforeEndTimeValidator implements ConstraintValidator<StartBeforeEnd, BusinessHourSaveRequest> {

    @Override
    public boolean isValid(BusinessHourSaveRequest request, ConstraintValidatorContext context) {
        if (request.getStartTime() == null || request.getEndTime() == null) {
            return false;
        }
        return request.getStartTime().isBefore(request.getEndTime());
    }
}
