package com.twogather.twogatherwebbackend.valid;


import com.twogather.twogatherwebbackend.dto.businesshour.BusinessHourSaveRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static com.twogather.twogatherwebbackend.TestConstants.BUSINESS_HOUR_SAVE_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
@RunWith(SpringRunner.class)
@SpringBootTest(classes= {ValidationAutoConfiguration.class})
public class BusinessHourValidationTest {
    @Autowired
    private Validator validator;
    @Test
    public void WhenValidBusinessHourRequest_ThenTrue() {
        // Given
        BusinessHourSaveRequest request = BUSINESS_HOUR_SAVE_REQUEST;

        // When
        Set<ConstraintViolation<BusinessHourSaveRequest>> violations = validator.validate(request);
        assertThat(violations.size()).isEqualTo(0);
        // Then
    }

    @Test
    public void WhenMissingOpenHours_ThenFalse() {
        // Given
        BusinessHourSaveRequest request = new BusinessHourSaveRequest(
                1L, null, null,
                DayOfWeek.MONDAY, true, false, null, null
        );

        // When
        Set<ConstraintViolation<BusinessHourSaveRequest>> violations = validator.validate(request);
        boolean violationsFound =
                violations.stream().anyMatch(
                        v -> v.getMessageTemplate().contains("Open hours must have both start time and end time"));
        assertThat(violationsFound).isTrue();
        // Then
    }
    @Test
    public void WhenStartTimeAfterEndTime_ThenViolation() {
        // Given
        BusinessHourSaveRequest request = new BusinessHourSaveRequest(
                1L, LocalTime.of(12, 0), LocalTime.of(11, 0),
                DayOfWeek.MONDAY, true, false, null, null
        );

        // When
        Set<ConstraintViolation<BusinessHourSaveRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<BusinessHourSaveRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Start time must be before end time");
    }

    @Test
    public void WhenBreakStartTimeAfterEndTime_ThenViolation() {
        // Given
        BusinessHourSaveRequest request = new BusinessHourSaveRequest(
                1L, LocalTime.of(9, 0), LocalTime.of(17, 0),
                DayOfWeek.MONDAY, true, true, LocalTime.of(14, 0), LocalTime.of(13, 0)
        );

        // When
        Set<ConstraintViolation<BusinessHourSaveRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<BusinessHourSaveRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Break start time must be before break end time");
    }

    @Test
    public void WhenBreakStartTimeEqualsEndTime_ThenViolation() {
        // Given
        BusinessHourSaveRequest request = new BusinessHourSaveRequest(
                1L, LocalTime.of(9, 0), LocalTime.of(17, 0),
                DayOfWeek.MONDAY, true, true, LocalTime.of(14, 0), LocalTime.of(14, 0)
        );

        // When
        Set<ConstraintViolation<BusinessHourSaveRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<BusinessHourSaveRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("Break start time must be before break end time");
    }
}
