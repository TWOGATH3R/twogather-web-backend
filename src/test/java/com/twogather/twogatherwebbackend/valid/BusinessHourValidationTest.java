package com.twogather.twogatherwebbackend.valid;


import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveUpdateRequest;
import com.twogather.twogatherwebbackend.exception.BusinessHourException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static com.twogather.twogatherwebbackend.TestConstants.BUSINESS_HOUR_SAVE_UPDATE_REQUEST;
import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.MUST_HAVE_START_TIME_AND_END_TIME;
import static com.twogather.twogatherwebbackend.exception.BusinessHourException.BusinessHourErrorCode.START_TIME_MUST_BE_BEFORE_END_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BusinessHourValidationTest {
    @Autowired
    private BusinessHourValidator validator;
    @Test
    public void WhenValidBusinessHourRequest_ThenTrue() {
        // Given
        BusinessHourSaveUpdateRequest request = BUSINESS_HOUR_SAVE_UPDATE_REQUEST;

        // When
        validator.validateBusinessHourRequest(request);
        // Then
    }

    @Test
    public void WhenMissingOpenHours_ThenFalse() {
        // Given
        BusinessHourSaveUpdateRequest request = new BusinessHourSaveUpdateRequest(
                1L, null, null,
                DayOfWeek.MONDAY, true, false, null, null
        );

        // When
        Throwable exception = assertThrows(BusinessHourException.class, () -> {
            validator.validateBusinessHourRequest(request);
        });
        // Then
        assertThat(exception.getMessage()).isEqualTo(MUST_HAVE_START_TIME_AND_END_TIME.getMessage());
    }
    @Test
    public void WhenStartTimeAfterEndTime_ThenViolation() {
        // Given
        BusinessHourSaveUpdateRequest request = new BusinessHourSaveUpdateRequest(
                1L, LocalTime.of(12, 0), LocalTime.of(11, 0),
                DayOfWeek.MONDAY, true, false, null, null
        );

        // When
        Throwable exception = assertThrows(BusinessHourException.class, () -> {
            validator.validateBusinessHourRequest(request);
        });
        assertThat(exception.getMessage()).isEqualTo(START_TIME_MUST_BE_BEFORE_END_TIME.getMessage());
    }

    @Test
    public void WhenBreakStartTimeAfterEndTime_ThenViolation() {
        // Given
        BusinessHourSaveUpdateRequest request = new BusinessHourSaveUpdateRequest(
                1L, LocalTime.of(9, 0), LocalTime.of(17, 0),
                DayOfWeek.MONDAY, true, true, LocalTime.of(14, 0), LocalTime.of(13, 0)
        );

        // When
        Throwable exception = assertThrows(BusinessHourException.class, () -> {
            validator.validateBusinessHourRequest(request);
        });
        assertThat(exception.getMessage()).isEqualTo(START_TIME_MUST_BE_BEFORE_END_TIME.getMessage());
    }

    @Test
    public void WhenBreakStartTimeEqualsEndTime_ThenViolation() {
        // Given
        BusinessHourSaveUpdateRequest request = new BusinessHourSaveUpdateRequest(
                1L, LocalTime.of(9, 0), LocalTime.of(17, 0),
                DayOfWeek.MONDAY, true, true, LocalTime.of(14, 0), LocalTime.of(14, 0)
        );

        // When
        Throwable exception = assertThrows(BusinessHourException.class, () -> {
            validator.validateBusinessHourRequest(request);
        });
        assertThat(exception.getMessage()).isEqualTo(START_TIME_MUST_BE_BEFORE_END_TIME.getMessage());
    }

}
