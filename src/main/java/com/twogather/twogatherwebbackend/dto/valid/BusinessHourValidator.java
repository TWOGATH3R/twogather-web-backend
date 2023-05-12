package com.twogather.twogatherwebbackend.dto.valid;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BusinessHourValidator implements ConstraintValidator<BusinessHourValidation, BusinessHourRequest> {

    //만약에 isopen이 false인 경우에는 서버에서 영업시간 관련 정볼르 그냥 무시하도록 해야겠음
    @Override
    public boolean isValid(BusinessHourRequest request, ConstraintValidatorContext context) {
        if (request.getIsOpen() && (request.getStartTime() == null || request.getEndTime() == null)) {
            addConstraintViolation(context, "startTime", "Open hours must have both start time and end time");
            return false;
        }

        if (request.getHasBreakTime() && (request.getBreakStartTime() == null || request.getBreakEndTime() == null)) {
            addConstraintViolation(context, "breakStartTime", "Break time must have both start time and end time");
            return false;
        }

        if (request.getStartTime() != null && request.getEndTime() != null && !request.getStartTime().isBefore(request.getEndTime())) {
            addConstraintViolation(context, "startTime", "Start time must be before end time");
            return false;
        }

        if (request.getBreakStartTime() != null && request.getBreakEndTime() != null && !request.getBreakStartTime().isBefore(request.getBreakEndTime())) {
            addConstraintViolation(context, "breakStartTime", "Break start time must be before break end time");
            return false;
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String fieldName, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}
