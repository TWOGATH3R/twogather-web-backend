package com.twogather.twogatherwebbackend.valid;

import com.twogather.twogatherwebbackend.dto.StartTimeBeforeEndTime;
import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.INVALID_TIME;

@Component
public class BusinessHourValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return StartTimeBeforeEndTime.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StartTimeBeforeEndTime dto = (StartTimeBeforeEndTime) target;
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            return;
        }
        boolean result = dto.getStartTime().isBefore(dto.getEndTime());
        if(!result){
            throw new BusinessHourException(INVALID_TIME);
        }
    }
}
