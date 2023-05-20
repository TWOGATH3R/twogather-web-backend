package com.twogather.twogatherwebbackend.valid;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourRequest;
import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;

import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.MUST_HAVE_START_TIME_AND_END_TIME;
import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.START_TIME_MUST_BE_BEFORE_END_TIME;

@Component
public class BusinessHourValidator {

    public void validateBusinessHourRequest(BusinessHourRequest request) {
        if (request.getIsOpen() && (request.getStartTime() == null || request.getEndTime() == null)) {
            throw new BusinessHourException(MUST_HAVE_START_TIME_AND_END_TIME);
        }

        if (request.getHasBreakTime() && (request.getBreakStartTime() == null || request.getBreakEndTime() == null)) {
            throw new BusinessHourException(MUST_HAVE_START_TIME_AND_END_TIME);
        }

        if (request.getStartTime() != null && request.getEndTime() != null && !request.getStartTime().isBefore(request.getEndTime())) {
            throw new BusinessHourException(START_TIME_MUST_BE_BEFORE_END_TIME);
        }

        if (request.getBreakStartTime() != null && request.getBreakEndTime() != null && !request.getBreakStartTime().isBefore(request.getBreakEndTime())) {
            throw new BusinessHourException(START_TIME_MUST_BE_BEFORE_END_TIME);
        }
    }

}
