package com.twogather.twogatherwebbackend.valid;
import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateInfo;
import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.MUST_HAVE_START_TIME_AND_END_TIME;
import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.START_TIME_MUST_BE_BEFORE_END_TIME;

@Component
@Slf4j
public class BusinessHourValidator {

    public void validateBusinessHourRequest(BusinessHourSaveUpdateInfo request) {
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
        log.info("businessHour: 유효성 검사 통과");
    }

}
